# Test Pages Styling Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply a single shared, on-brand stylesheet plus a "Selenide" header banner to ~70 HTML test fixtures in `src/test/resources/`, leaving 5 pages whose look/geometry is itself under test untouched.

**Architecture:** One new file `src/test/resources/selenide-test.css` is referenced from each styled page via `<link rel="stylesheet" href="/selenide-test.css">` in `<head>`, plus a `<div class="selenide-test-banner">Selenide</div>` injected as the first child of `<body>`. Bulk edits are performed by a one-shot Python script driven from a hard-coded skip list. Verification is by re-running the existing unit and chrome-headless integration test suites.

**Tech Stack:** Plain HTML5/CSS3, Inter web font from Google Fonts, Bash + Python 3 for the bulk transformation, Gradle for verification.

**Branch:** `style-test-pages` (already created).

**Spec:** `docs/superpowers/specs/2026-05-07-test-pages-styling-design.md`

---

## Task 1: Write the shared stylesheet

**Files:**
- Create: `src/test/resources/selenide-test.css`

- [ ] **Step 1: Create the stylesheet with the full design**

Write the file `src/test/resources/selenide-test.css` with this exact content:

```css
/* Shared cosmetic styling for Selenide test fixture HTML pages.
   Applied via <link rel="stylesheet" href="/selenide-test.css">. */

@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

body {
  margin: 0;
  padding: 16px 24px;
  background: #F5FAFC;
  color: #393939;
  font: 14px/1.5 'Inter', "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.selenide-test-banner {
  background: #303030;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  padding: 10px 24px;
  margin: -16px -24px 20px -24px;
  letter-spacing: 0.02em;
}

h1, h2, h3, h4, h5, h6 {
  margin: 0 0 16px;
  line-height: 1.2;
}
h1 { font-size: 28px; color: #222; }
h2 { font-size: 22px; color: #393939; }
h3 { font-size: 18px; color: #494949; }

p, ul, ol, dl {
  margin: 0 0 16px;
}

a {
  color: #46A3D1;
  text-decoration: none;
}
a:hover {
  text-decoration: underline;
}

input[type=text], input[type=email], input[type=password], input[type=search],
input[type=number], input[type=tel], input[type=url],
input[type=date], input[type=datetime-local], input[type=time], input[type=month], input[type=week],
input:not([type]),
select, textarea {
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 6px 10px;
  font: inherit;
  color: #393939;
  background: #fff;
}

input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: #46A3D1;
  box-shadow: 0 0 0 3px rgba(70, 163, 209, 0.15);
}

button, input[type=button], input[type=submit], input[type=reset] {
  background: #46A3D1;
  color: #fff;
  border: 1px solid #46A3D1;
  border-radius: 4px;
  padding: 6px 14px;
  font: inherit;
  font-weight: 500;
  cursor: pointer;
}

button:hover, input[type=button]:hover,
input[type=submit]:hover, input[type=reset]:hover {
  background: #3a8ab2;
  border-color: #3a8ab2;
}

button:disabled, input[type=button]:disabled,
input[type=submit]:disabled, input[type=reset]:disabled {
  background: #cccccc;
  border-color: #cccccc;
  color: #777;
  cursor: not-allowed;
}

table {
  border-collapse: collapse;
  margin: 0 0 16px;
}
th, td {
  text-align: left;
  padding: 6px 10px;
  border-bottom: 1px solid #e5e5e5;
}
th { color: #222; font-weight: 600; }

fieldset {
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 12px 16px;
  margin: 0 0 16px;
}
legend {
  color: #494949;
  padding: 0 6px;
}

code {
  font-family: Monaco, "Bitstream Vera Sans Mono", "Lucida Console", monospace;
  font-size: 12px;
  color: #333;
}
pre {
  background: #ffffff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 8px 12px;
  overflow-x: auto;
}
```

- [ ] **Step 2: Commit**

```bash
git add src/test/resources/selenide-test.css
git commit -m "test resources: add shared selenide-test.css"
```

---

## Task 2: Apply to three sample pages and eyeball them

Verify the CSS works before bulk-applying.

**Files:**
- Modify: `src/test/resources/start_page.html`
- Modify: `src/test/resources/page_with_alerts.html`
- Modify: `src/test/resources/autocomplete.html`

- [ ] **Step 1: Edit `src/test/resources/start_page.html`**

Replace the file's entire content with:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <title>start page</title>
  <meta charset="UTF-8">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <link rel="stylesheet" href="/selenide-test.css">
</head>
<body>
<div class="selenide-test-banner">Selenide</div>
<h1>Selenide</h1>
<h2 id="start-selenide">Start page</h2>

<div>
  <span id="greeting">Hello_my_friend</span>
</div>

</body>
</html>
```

- [ ] **Step 2: Edit `src/test/resources/page_with_alerts.html`**

Apply two changes to the existing file:

1. Change `<html lang="ru">` to `<html lang="en">` and remove the
   `<meta http-equiv="Content-Language" content="ru"/>` line.
2. Insert `<link rel="stylesheet" href="/selenide-test.css">` as the last
   line of `<head>` (after `</script>` and before `</head>`).
3. Insert `<div class="selenide-test-banner">Selenide</div>` as the first
   line after `<body>`.

After the edit, the file's `<head>` block ends with:

```html
  </script>
  <link rel="stylesheet" href="/selenide-test.css">
</head>
```

…and the `<body>` block starts with:

```html
<body>
<div class="selenide-test-banner">Selenide</div>
<h1>Page with alerts</h1>
```

- [ ] **Step 3: Edit `src/test/resources/autocomplete.html`**

Insert the link before `</head>` and the banner div as the first child of
`<body>`. Do not change anything else.

- [ ] **Step 4: Eyeball the three pages**

Start the existing test HTTPS server by running any one integration test that
opens these pages, OR run a one-shot static server:

```bash
cd /Users/andrei/projects/selenide/src/test/resources
python3 -m http.server 8765
```

Open in a browser:
- http://localhost:8765/start_page.html
- http://localhost:8765/page_with_alerts.html
- http://localhost:8765/autocomplete.html

Expected: dark "Selenide" banner across the top, light blue background, Inter
font, blue buttons on `page_with_alerts.html`. Stop the server (Ctrl+C).

- [ ] **Step 5: Commit**

```bash
git add src/test/resources/start_page.html \
        src/test/resources/page_with_alerts.html \
        src/test/resources/autocomplete.html
git commit -m "test resources: style start_page, page_with_alerts, autocomplete"
```

---

## Task 3: Bulk-apply to remaining "ordinary" pages via script

**Files:**
- Create: `tools/style_test_pages.py` (temporary, deleted at the end of this task)
- Modify: most `.html` files in `src/test/resources/`

- [ ] **Step 1: Write the transform script**

Create `tools/style_test_pages.py` with this content:

```python
#!/usr/bin/env python3
"""One-shot transform: add link + banner to test fixture HTML pages.

Skip rules:
- Pages in SKIP_FULL: do not touch at all.
- Pages in SKIP_BANNER: add link only (no banner) — frameset pages.
- Pages in CUSTOM: leave for manual handling.
- All others: insert <link> immediately before </head>, insert banner
  div as the first child of <body>, and rewrite lang="ru" -> "en".
- Already-styled files (containing 'selenide-test.css') are skipped silently.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "src" / "test" / "resources"

SKIP_FULL = {
    "page_with_big_divs.html",
    "page_with_responsive_ui.html",
    "page_with_moving_elements.html",
    "page_with_scroll_element.html",
    "empty.html",
}

SKIP_BANNER = {
    "page_with_frames.html",
    "page_with_parent_frame.html",
    "page_with_child_frame.html",
}

CUSTOM = {
    "page_with_iframe.html",  # has no <body>; handled in Task 4
}

LINK_TAG = '<link rel="stylesheet" href="/selenide-test.css">'
BANNER = '<div class="selenide-test-banner">Selenide</div>'


def transform(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    if "selenide-test.css" in text:
        return False  # already done

    name = path.name
    if name in SKIP_FULL or name in CUSTOM:
        return False

    # lang="ru" -> "en" (also drop Content-Language=ru meta if present)
    text = re.sub(r'<html\s+lang="ru"', '<html lang="en"', text)
    text = re.sub(
        r'\s*<meta\s+http-equiv="Content-Language"\s+content="ru"\s*/?>\s*\n?',
        "\n",
        text,
        flags=re.IGNORECASE,
    )

    # Insert <link> immediately before </head>.
    if "</head>" not in text:
        print(f"  WARN: no </head> in {name}, skipping link", file=sys.stderr)
    else:
        text = text.replace(
            "</head>",
            f"  {LINK_TAG}\n</head>",
            1,
        )

    if name in SKIP_BANNER:
        path.write_text(text, encoding="utf-8")
        return True

    # Insert banner as first thing inside <body ...>.
    m = re.search(r"<body\b[^>]*>", text)
    if not m:
        print(f"  WARN: no <body> in {name}, no banner inserted", file=sys.stderr)
    else:
        end = m.end()
        text = text[:end] + "\n" + BANNER + text[end:]

    path.write_text(text, encoding="utf-8")
    return True


def main() -> int:
    changed = 0
    for path in sorted(RES.glob("*.html")):
        if transform(path):
            print(f"  styled: {path.name}")
            changed += 1
    print(f"\n{changed} file(s) updated.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
```

- [ ] **Step 2: Run the script**

```bash
cd /Users/andrei/projects/selenide
python3 tools/style_test_pages.py
```

Expected output: a list of styled file names (around 60), then a count line.
Files in `SKIP_FULL`, `CUSTOM`, and the three pages already styled in Task 2
are NOT in the list.

- [ ] **Step 3: Spot-check three modified files**

Open these three files and confirm both the link and banner are present:

```bash
grep -l 'selenide-test.css' src/test/resources/page_with_jquery.html \
                              src/test/resources/page_with_disabled_elements.html \
                              src/test/resources/page_with_tabs.html
```

Expected: all three paths printed (one per line).

```bash
grep 'selenide-test-banner' src/test/resources/page_with_jquery.html
```

Expected: one match showing the banner div.

- [ ] **Step 4: Verify skip-list pages were NOT touched**

```bash
for f in page_with_big_divs.html page_with_responsive_ui.html \
         page_with_moving_elements.html page_with_scroll_element.html \
         empty.html page_with_iframe.html; do
  if grep -q 'selenide-test.css' "src/test/resources/$f"; then
    echo "FAIL: $f should not have been styled"
  else
    echo "OK:   $f untouched"
  fi
done
```

Expected: six "OK:" lines.

- [ ] **Step 5: Verify lang="ru" was rewritten**

```bash
grep -l 'lang="ru"' src/test/resources/*.html
```

Expected: no output.

- [ ] **Step 6: Delete the script (it's a one-shot)**

```bash
rm tools/style_test_pages.py
rmdir tools 2>/dev/null || true
```

- [ ] **Step 7: Commit**

```bash
git add src/test/resources/*.html
git commit -m "test resources: bulk-apply selenide-test.css and Selenide banner"
```

---

## Task 4: Hand-edit `page_with_iframe.html`

**Files:**
- Modify: `src/test/resources/page_with_iframe.html`

The page currently has no `<body>` — its `<iframe>` sits directly under
`<html>`. Wrap it properly.

- [ ] **Step 1: Replace the file**

Replace the entire content of `src/test/resources/page_with_iframe.html` with:

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Test::iframe</title>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/selenide-test.css">
  </head>
  <body>
    <div class="selenide-test-banner">Selenide</div>
    <iframe src="page_with_big_divs.html" height="500" width="300" id="iframe_page" style="border:1px"></iframe>
  </body>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/test/resources/page_with_iframe.html
git commit -m "test resources: wrap iframe in body and style page_with_iframe"
```

---

## Task 5: Run unit tests

- [ ] **Step 1: Run unit tests**

```bash
cd /Users/andrei/projects/selenide
./gradlew check
```

Expected: BUILD SUCCESSFUL. No new failures vs. main.

If failures occur, they're more likely caused by Checkstyle/SpotBugs noticing
something unrelated — investigate and fix in place. Do NOT proceed to Task 6
until `./gradlew check` is green.

- [ ] **Step 2: Commit any incidental fixes (if any)**

```bash
git status
# If anything new: git add -p && git commit -m "test: <fix description>"
```

---

## Task 6: Run integration tests and triage failures

This is where breakage is most likely.

- [ ] **Step 1: Run the chrome-headless integration suite**

```bash
cd /Users/andrei/projects/selenide
./gradlew chrome_headless
```

Expected on first run: some failures. Capture the failed test report path
from Gradle output (typically
`build/reports/tests/chrome_headless/index.html`).

- [ ] **Step 2: Triage failures one by one**

For each failing test, decide between two fix strategies:

**Strategy A — Add the page to the skip list.** If the failure is caused
solely by the banner, link, or body padding interfering with a single page's
geometry/screenshot, revert the styling on that page only.

To revert one page:

```bash
git diff main -- src/test/resources/<page>.html  # see the diff
git checkout main -- src/test/resources/<page>.html
```

Then add the page name to the spec's skip list (in
`docs/superpowers/specs/2026-05-07-test-pages-styling-design.md`) so the
documented list stays in sync with reality.

**Strategy B — Fix the test.** If the test is asserting on something we
intentionally changed (font, link color, button background), update the test
to reflect the new expected value. The user explicitly approved this for
"medium polish" branded buttons.

**Strategy C — Restructure the page.** Only used for `<frameset>`-related
quirks; usually unnecessary.

- [ ] **Step 3: Re-run until green**

```bash
./gradlew chrome_headless
```

Repeat triage until BUILD SUCCESSFUL.

- [ ] **Step 4: Commit fixes**

Each meaningful fix gets its own commit. Examples:

```bash
# A skip-list addition:
git add src/test/resources/page_with_X.html \
        docs/superpowers/specs/2026-05-07-test-pages-styling-design.md
git commit -m "test resources: skip styling on page_with_X (test asserts on Y)"

# A test fix:
git add src/test/java/integration/SomeTest.java
git commit -m "test: update SomeTest to expect Selenide-blue button background"
```

---

## Task 7: Verify javadocForSite and finalize

- [ ] **Step 1: Run javadocForSite (required by `CLAUDE.md`)**

```bash
cd /Users/andrei/projects/selenide
./gradlew javadocForSite
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 2: Final visual sanity check**

Re-open three pages in a browser and confirm they still look right:

```bash
cd /Users/andrei/projects/selenide/src/test/resources
python3 -m http.server 8765
```

Open and eyeball:
- http://localhost:8765/page_with_jquery.html
- http://localhost:8765/page_with_inputs_and_hints.html
- http://localhost:8765/page_with_tabs.html

Stop the server.

- [ ] **Step 3: Review the branch summary**

```bash
git log main..HEAD --oneline
git diff main --stat | tail -5
```

Expected: the commits from Tasks 1–7 listed; the diffstat shows the new
`selenide-test.css` plus modifications scattered across most `.html` test
fixtures.

- [ ] **Step 4: Push the branch (only if requested by the user)**

```bash
# Do NOT push without the user asking. When asked:
git push -u origin style-test-pages
```

---

## Final acceptance checklist

- [ ] `src/test/resources/selenide-test.css` exists with the design content.
- [ ] Every styled page contains `<link rel="stylesheet" href="/selenide-test.css">` and `<div class="selenide-test-banner">Selenide</div>` (except frameset pages, which have only the link).
- [ ] The five hard-skip pages (`page_with_big_divs`, `page_with_responsive_ui`, `page_with_moving_elements`, `page_with_scroll_element`, `empty`) are byte-identical to `main`.
- [ ] No HTML file contains `lang="ru"`.
- [ ] `./gradlew check` passes.
- [ ] `./gradlew chrome_headless` passes.
- [ ] `./gradlew javadocForSite` passes.
- [ ] Spec skip list in `docs/superpowers/specs/2026-05-07-test-pages-styling-design.md` reflects any pages added during Task 6 triage.
