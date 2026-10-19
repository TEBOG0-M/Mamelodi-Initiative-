<?php
declare(strict_types=1);
require_once __DIR__ . '/../vendor/autoload.php';

use Firebase\JWT\JWT;
use Firebase\JWT\Key;

function env(string $k, string $d = ''): string {
    $v = getenv($k);
    return ($v === false || $v === '') ? $d : $v;
}

function json_out(int $code, array $data): never {
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function fail(int $code, string $msg, array $fields = []): never {
    json_out($code, ['error' => $msg] + ($fields ? ['fields' => $fields] : []));
}

function cors(): void {
    $origin  = $_SERVER['HTTP_ORIGIN'] ?? '';
    $allowed = array_filter(array_map('trim', explode(',', env('ALLOWED_ORIGINS'))));
    if ($origin !== '' && in_array($origin, $allowed, true)) {
        header("Access-Control-Allow-Origin: $origin");
        header('Vary: Origin');
        header('Access-Control-Allow-Headers: Authorization, Content-Type');
        header('Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS');
    }
    if (($_SERVER['REQUEST_METHOD'] ?? '') === 'OPTIONS') { http_response_code(204); exit; }
}

function db(): PDO {
    static $pdo = null;
    if ($pdo) return $pdo;
    $dsn = sprintf('mysql:host=%s;port=%s;dbname=%s;charset=utf8mb4',
        env('DB_HOST', '127.0.0.1'), env('DB_PORT', '3306'), env('DB_NAME', 'mamelodi'));
    $opt = [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false];
    if (env('DB_SSL_CA') !== '') $opt[PDO::MYSQL_ATTR_SSL_CA] = env('DB_SSL_CA');
    return $pdo = new PDO($dsn, env('DB_USER'), env('DB_PASS'), $opt);
}

/* ---------- Firebase ID-token verification (no secrets needed) ---------- */

function google_certs(): array {
    $f = sys_get_temp_dir() . '/fb_certs.json';
    if (is_file($f) && time() - filemtime($f) < 3600) {
        $c = json_decode((string)file_get_contents($f), true);
        if (is_array($c)) return $c;
    }
    $raw = @file_get_contents(
        'https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com',
        false, stream_context_create(['http' => ['timeout' => 5]]));
    $c = $raw ? json_decode($raw, true) : null;
    if (is_array($c)) { @file_put_contents($f, $raw); return $c; }
    if (is_file($f)) { $c = json_decode((string)file_get_contents($f), true); if (is_array($c)) return $c; }
    fail(503, 'Authentication service unavailable');
}

function bearer_token(): string {
    $h = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? '';
    return preg_match('/^Bearer\s+(\S+)$/i', $h, $m) ? $m[1] : '';
}

/** Verified claims -> user, or throws. Pure function so it can be unit tested. */
function check_claims(array $c, string $projectId): array {
    if (($c['aud'] ?? '') !== $projectId
        || ($c['iss'] ?? '') !== "https://securetoken.google.com/$projectId"
        || empty($c['sub']) || !is_string($c['sub']) || strlen($c['sub']) > 128) {
        throw new RuntimeException('bad claims');
    }
    return ['uid' => $c['sub'], 'email' => (string)($c['email'] ?? ''), 'name' => (string)($c['name'] ?? '')];
}

function require_user(): array {
    $t = bearer_token();
    if ($t === '') fail(401, 'Missing bearer token');
    $pid = env('FIREBASE_PROJECT_ID');
    if ($pid === '') fail(500, 'Server is not configured');
    if (substr_count($t, '.') !== 2) fail(401, 'Invalid or expired token');   // not a JWT: reject before any network call
    try {
        $keys = [];
        foreach (google_certs() as $kid => $pem) $keys[$kid] = new Key($pem, 'RS256');
        JWT::$leeway = 60;
        $claims = (array)JWT::decode($t, $keys);
        return check_claims($claims, $pid);
    } catch (Throwable $e) {
        fail(401, 'Invalid or expired token');
    }
}

function upsert_profile(array $u): void {
    db()->prepare(
        "INSERT INTO user_profiles (firebase_uid, email, display_name) VALUES (?,?,?)
         ON DUPLICATE KEY UPDATE email = VALUES(email),
         display_name = COALESCE(NULLIF(VALUES(display_name), ''), display_name)"
    )->execute([$u['uid'], $u['email'], mb_substr($u['name'], 0, 100)]);
}

/* ---------- Validation ---------- */

const PROGRAMMES = ['summer_jam', 'winter_jam', 'road_to_finals', 'ylc'];
const SUBJECTS   = ['Mathematics', 'Physical Sciences', 'Life Sciences', 'English FAL', 'Coding / IT', 'Creative Arts'];
const AVAILABILITY = ['Saturday Mornings', 'School Holiday Camps', 'Weekday Afternoons'];

function validate_application(array $d): array {
    $e = []; $out = [];
    $str = fn(string $k) => is_string($d[$k] ?? null) ? trim($d[$k]) : '';
    $len = fn(string $s) => mb_strlen($s);

    $out['full_name'] = $str('full_name');
    if ($len($out['full_name']) < 2 || $len($out['full_name']) > 100) $e['full_name'] = 'Enter your full name (2-100 characters).';

    $out['email'] = $str('email');
    if (!filter_var($out['email'], FILTER_VALIDATE_EMAIL) || $len($out['email']) > 254) $e['email'] = 'Enter a valid email address.';

    $out['phone'] = $str('phone');
    if (!preg_match('/^\+?[0-9 ()\-]{7,20}$/', $out['phone'])) $e['phone'] = 'Enter a valid phone number.';

    foreach (['qualification' => 'highest qualification', 'field_of_study' => 'field of study'] as $k => $label) {
        $out[$k] = $str($k);
        if ($len($out[$k]) < 2 || $len($out[$k]) > 120) $e[$k] = "Enter your $label.";
    }

    $out['programme_preference'] = $str('programme_preference');
    if (!in_array($out['programme_preference'], PROGRAMMES, true)) $e['programme_preference'] = 'Choose a programme.';

    foreach (['subjects' => SUBJECTS, 'availability' => AVAILABILITY] as $k => $allowed) {
        $v = $d[$k] ?? null;
        if (!is_array($v) || !$v || array_diff($v, $allowed) || count($v) > count($allowed)) {
            $e[$k] = 'Select at least one valid option.'; $out[$k] = [];
        } else $out[$k] = array_values(array_unique($v));
    }

    $out['motivation'] = $str('motivation');
    if ($len($out['motivation']) < 20 || $len($out['motivation']) > 1000) $e['motivation'] = 'Tell us why you want to volunteer (20-1000 characters).';

    if (($d['consent_background_check'] ?? false) !== true) $e['consent_background_check'] = 'You must accept the background-check statement.';
    return [$out, $e];
}

function validate_contact(array $d): array {
    $e = []; $out = [];
    $str = fn(string $k) => is_string($d[$k] ?? null) ? trim($d[$k]) : '';
    $out['name'] = $str('name');       if (mb_strlen($out['name']) < 2 || mb_strlen($out['name']) > 100) $e['name'] = 'Enter your name.';
    $out['email'] = $str('email');     if (!filter_var($out['email'], FILTER_VALIDATE_EMAIL)) $e['email'] = 'Enter a valid email address.';
    $out['subject'] = $str('subject'); if (mb_strlen($out['subject']) < 3 || mb_strlen($out['subject']) > 150) $e['subject'] = 'Enter a subject (3-150 characters).';
    $out['message'] = $str('message'); if (mb_strlen($out['message']) < 10 || mb_strlen($out['message']) > 2000) $e['message'] = 'Message must be 10-2000 characters.';
    return [$out, $e];
}

function app_row(array $r): array {   // never exposes firebase_uid
    return [
        'id' => (int)$r['id'], 'full_name' => $r['full_name'], 'email' => $r['email'], 'phone' => $r['phone'],
        'qualification' => $r['qualification'], 'field_of_study' => $r['field_of_study'],
        'programme_preference' => $r['programme_preference'],
        'subjects' => json_decode($r['subjects'], true) ?: [], 'availability' => json_decode($r['availability'], true) ?: [],
        'motivation' => $r['motivation'], 'status' => $r['status'], 'created_at' => $r['created_at'],
    ];
}