import { readFileSync, readdirSync, existsSync, statSync, writeFileSync, mkdtempSync } from "node:fs";
import { join, dirname, resolve } from "node:path";
import { tmpdir } from "node:os";
import { spawnSync } from "node:child_process";

const SITE = "website";
const walk = (d) => readdirSync(d).flatMap((n) => {
  const p = join(d, n); return statSync(p).isDirectory() ? walk(p) : [p];
});
const files = walk(SITE);
const errors = [];

for (const f of files.filter((x) => x.endsWith(".html"))) {
  const html = readFileSync(f, "utf8");
  for (const m of html.matchAll(/\b(?:href|src)="([^"]*)"/g)) {
    const url = m[1];
    if (url === "#") { errors.push(`${f}: dead link href="#"`); continue; }
    if (/^(https?:|mailto:|tel:|data:|#)/.test(url) || url.includes("${")) continue;
    const target = resolve(dirname(f), url.split("#")[0].split("?")[0]);
    if (!existsSync(target)) errors.push(`${f}: broken link -> ${url}`);
  }
  if (!/<title>[^<]+<\/title>/.test(html)) errors.push(`${f}: missing <title>`);
  if (!/name="viewport"/.test(html)) errors.push(`${f}: missing viewport meta`);
}

const tmp = mkdtempSync(join(tmpdir(), "chk-"));
for (const f of files.filter((x) => x.endsWith(".js"))) {
  const copy = join(tmp, "m.mjs");
  writeFileSync(copy, readFileSync(f));
  const r = spawnSync(process.execPath, ["--check", copy], { encoding: "utf8" });
  if (r.status !== 0) errors.push(`${f}: JS syntax error\n${r.stderr}`);
}

const pages = files.filter((x) => x.endsWith(".html")).length;
if (errors.length) { console.error(errors.join("\n")); console.error(`\n${errors.length} problem(s) in ${pages} page(s)`); process.exit(1); }
console.log(`OK: ${pages} pages, ${files.filter((x) => x.endsWith(".js")).length} scripts checked`);