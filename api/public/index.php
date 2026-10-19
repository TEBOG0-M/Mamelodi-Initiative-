<?php
declare(strict_types=1);
require __DIR__ . '/../src/lib.php';

cors();
header('X-Content-Type-Options: nosniff');

$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$path   = rtrim((string)parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH), '/') ?: '/';
$prefix = env('API_PREFIX', '/api');
if ($prefix !== '' && str_starts_with($path, $prefix)) $path = substr($path, strlen($prefix)) ?: '/';

function body(): array {
    $raw = file_get_contents('php://input');
    if ($raw === false || strlen($raw) > 20000) fail(413, 'Request body too large');
    $d = json_decode($raw, true);
    if (!is_array($d)) fail(400, 'Body must be valid JSON');
    return $d;
}

try {
    if ($method === 'GET' && $path === '/health') json_out(200, ['status' => 'ok']);

    if ($method === 'GET' && $path === '/news') {
        $rows = db()->query("SELECT id,title,slug,category,summary,published_at,is_sample FROM news_articles ORDER BY published_at DESC, id DESC")->fetchAll();
        json_out(200, ['items' => $rows]);
    }
    if ($method === 'GET' && preg_match('#^/news/(\d+)$#', $path, $m)) {
        $s = db()->prepare("SELECT id,title,slug,category,summary,body,published_at,is_sample FROM news_articles WHERE id = ?");
        $s->execute([(int)$m[1]]);
        $r = $s->fetch() ?: fail(404, 'Article not found');
        json_out(200, $r);
    }

    if ($method === 'POST' && $path === '/contact') {
        $d = body();
        if (!empty($d['website'])) json_out(201, ['ok' => true]);        // honeypot: bots fill this
        [$c, $e] = validate_contact($d);
        if ($e) fail(422, 'Please correct the highlighted fields.', $e);
        db()->prepare("INSERT INTO contact_messages (name,email,subject,message) VALUES (?,?,?,?)")
            ->execute([$c['name'], $c['email'], $c['subject'], $c['message']]);
        json_out(201, ['ok' => true]);
    }

    /* ----- everything below needs a verified Firebase ID token ----- */
    if ($method === 'GET' && $path === '/me') {
        $u = require_user(); upsert_profile($u);
        json_out(200, ['email' => $u['email'], 'display_name' => $u['name']]);
    }

    if ($path === '/volunteer-applications' && $method === 'POST') {
        $u = require_user();
        [$a, $e] = validate_application(body());
        if ($e) fail(422, 'Please correct the highlighted fields.', $e);
        upsert_profile($u);
        $dup = db()->prepare("SELECT 1 FROM volunteer_applications WHERE firebase_uid=? AND programme_preference=? AND status<>'withdrawn'");
        $dup->execute([$u['uid'], $a['programme_preference']]);
        if ($dup->fetch()) fail(409, 'You already have an active application for this programme.');
        $ins = db()->prepare("INSERT INTO volunteer_applications
            (firebase_uid,full_name,email,phone,qualification,field_of_study,programme_preference,subjects,availability,motivation,consent_background_check)
            VALUES (?,?,?,?,?,?,?,?,?,?,1)");
        $ins->execute([$u['uid'], $a['full_name'], $a['email'], $a['phone'], $a['qualification'], $a['field_of_study'],
            $a['programme_preference'], json_encode($a['subjects']), json_encode($a['availability']), $a['motivation']]);
        $id = (int)db()->lastInsertId();
        $row = db()->prepare("SELECT * FROM volunteer_applications WHERE id=? AND firebase_uid=?");
        $row->execute([$id, $u['uid']]);
        json_out(201, app_row($row->fetch()));      // success only after the row is saved and read back
    }

    if ($path === '/volunteer-applications' && $method === 'GET') {
        $u = require_user();
        $s = db()->prepare("SELECT * FROM volunteer_applications WHERE firebase_uid=? ORDER BY created_at DESC");
        $s->execute([$u['uid']]);
        json_out(200, ['items' => array_map('app_row', $s->fetchAll())]);
    }

    if (preg_match('#^/volunteer-applications/(\d+)$#', $path, $m)) {
        $u = require_user(); $id = (int)$m[1];
        $s = db()->prepare("SELECT * FROM volunteer_applications WHERE id=? AND firebase_uid=?"); // ownership enforced in the query
        $s->execute([$id, $u['uid']]);
        $row = $s->fetch() ?: fail(404, 'Application not found');   // 404 (not 403) so other users' ids are not revealed
        if ($method === 'GET') json_out(200, app_row($row));
        if ($method === 'DELETE') {
            db()->prepare("UPDATE volunteer_applications SET status='withdrawn' WHERE id=? AND firebase_uid=?")->execute([$id, $u['uid']]);
            json_out(200, ['ok' => true, 'status' => 'withdrawn']);
        }
    }

    fail(404, 'Not found');
} catch (PDOException $e) {
    error_log('DB error: ' . $e->getMessage());
    fail(500, 'A server error occurred. Please try again later.');
}