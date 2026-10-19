import { readFileSync, writeFileSync } from "node:fs";

function convert(file, root, page) {
  let h = readFileSync(file, "utf8").replace(/\r\n/g, "\n");
  if (h.includes('id="site-header"')) return console.log("skip (already converted):", file);
  h = h.replace(/<header>[\s\S]*?<\/header>/, '<div id="site-header"></div>');
  h = h.replace(/<footer>[\s\S]*?<\/footer>/, '<div id="site-footer"></div>');
  h = h.replace(/<body[^>]*>/, `<body data-root="${root}" data-page="${page}">`);
  h = h.replace("</body>", `  <script type="module" src="${root}assets/js/components.js"></script>\n</body>`);
  if (!h.includes("viewport")) h = h.replace("<title>", '<meta name="viewport" content="width=device-width, initial-scale=1">\n  <title>');
  h = h.replace(/<div class="progress-container">[\s\S]*?(?=\s*<main)/, "");
  h = h.replace(/<img[^>]*picsum\.photos[^>]*>/g, "");
  h = h.replace(/href="(?:pages\/)?contact\.html" class="btn-donate">Donate Now/g, `href="${root}pages/donate.html" class="btn-donate">Donate Now`);
  h = h.replace(/href="(?:pages\/)?contact\.html" class="btn-donate">Volunteer/g, `href="${root}pages/volunteer.html" class="btn-donate">Volunteer`);
  writeFileSync(file, h);
  console.log("converted:", file);
}
convert("website/index.html", "", "home");
convert("website/pages/about.html", "../", "about");