# Styling for Selenide test HTML pages

## Goal

Make the ~70 HTML test pages in `src/test/resources/` look pleasant and on-brand
with selenide.org, without changing the meaning of any existing test (selectors,
text content, IDs, classes, structure).

The pages are functional fixtures used by integration tests; the styling is
purely cosmetic. A handful of pages must be left untouched because their
appearance or geometry is itself under test.

## Scope

- **In scope**: typography, colors, banner, body padding, form-control look,
  link/table styling — applied site-wide via one shared stylesheet linked from
  most pages.
- **Out of scope**: changes to JavaScript behavior, IDs/classes/text content,
  anything that alters element identity from a Selenium selector's point of
  view, and the test infrastructure itself.

## Delivery mechanism

A single shared stylesheet:

```
src/test/resources/selenide-test.css
```

Each "styled" page gets:

1. One `<link rel="stylesheet" href="/selenide-test.css">` line in `<head>`.
   The path is absolute so embedded/relative-loaded pages still resolve.
   If the test HTTPS server (`BaseIntegrationTest`) does not serve from the
   filesystem root, switch to a relative path (`selenide-test.css`).
2. A banner element as the first child of `<body>`:
   ```html
   <div class="selenide-test-banner">Selenide</div>
   ```

No JavaScript. No external network requests at runtime except the Google
Fonts import inside the CSS file (with system-font fallback so offline tests
still render).

## Visual design

Brand colors taken from
`selenide-web/assets/themes/ingmar/css/styles.css`:

| Role | Color |
|---|---|
| Brand blue (buttons, focus ring, links) | `#46A3D1` |
| Brand blue hover | `#3a8ab2` |
| Banner background | `#303030` |
| Banner text | `#ffffff` |
| Heading dark | `#222222` |
| Body text | `#393939` |
| Muted text | `#777777` |
| Page surface | `#F5FAFC` |
| Border | `#e5e5e5` |

Font: Inter (Google Fonts), with `"Helvetica Neue", Helvetica, Arial,
sans-serif` fallback.

Layout:

- `body { padding: 16px 24px; background: #F5FAFC; font: 14px/1.5 Inter,...; color: #393939; }`
- `.selenide-test-banner` is a dark bar at the very top, full bleed:
  `background: #303030; color: #fff; font-weight: 600; padding: 10px 24px;
  margin: -16px -24px 20px -24px;` (negative margins cancel body padding so it
  reaches the edges).

Components:

- Headings: `h1` 28px/dark; `h2` `#393939`; `h3+` `#494949`.
- Links: blue, no underline at rest, underline on hover.
- Inputs/selects/textareas: 1px `#e5e5e5` border, 4px radius, ~6×10px padding,
  focus → blue border + soft blue box-shadow.
- Buttons (`button`, `input[type=button|submit|reset]`): blue background,
  white text, 4px radius, 6×14px padding, hover `#3a8ab2`. Disabled: light
  gray.
- Tables: bottom-border separators on `th/td`; `th` darker.
- `code`/`pre`: monospace; `pre` gets a white background and a soft border.

## Pages NOT to style

The skip list is intentionally minimal. Only pages that we are certain would
be visually broken or whose CSS geometry is asserted in a way that no
restructuring can preserve are excluded:

- `page_with_big_divs.html` — explicitly flagged by the user; layout is the
  test fixture itself.
- `page_with_responsive_ui.html` — tests `body` background per viewport
  (`pink` on mobile, `#eeeeee` on desktop); our body background would override
  this.
- `page_with_moving_elements.html` — JavaScript animation depends on `body
  { margin: 0 }`; our body padding shifts coordinates and breaks the
  animation envelope.
- `page_with_scroll_element.html` — tests scroll geometry and absolute
  positions inside an overflow container; banner + body padding would shift
  measurements.
- `empty.html` — explicit `background-color: lightgrey` on `<body>` is part of
  the test.
- `download.html` — bare two-line snippet with no `<html>`, `<head>`, or
  `<body>`; nowhere to attach the `<link>` or banner without altering its
  malformed-page semantics.

All other HTML pages in `src/test/resources/` are styled, **including**:

- `animations.html`, `draggable.html`, `hover.html`
- `page_with_relative_click_position.html`, `page_with_pseudo_elements.html`,
  `page_with_transparent_elements.html`, `page_with_images.html`,
  `page_with_svg.html`, `screenshot.html`, `page_for_printing.html`
- `page_with_iframe.html`, `page_with_frames.html`,
  `page_with_parent_frame.html`, `page_with_child_frame.html`

Special handling for the latter group:

- `page_with_iframe.html` and `page_with_shadow_dom_inside_iframe.html`
  currently have content (`<iframe>` + script) directly between `</head>`
  and `</html>` with no `<body>`. We wrap them in a proper `<body>` and add
  link + banner.
- `page_with_frames.html`, `page_with_parent_frame.html`,
  `page_with_child_frame.html` use `<frameset>` and have no `<body>`.
  Frameset pages cannot host the banner. We add the `<link>` to `<head>`
  for consistency (it has no visible effect) and skip the banner. This is
  acknowledged as cosmetic-only inclusion.
- `page_for_printing.html` already sets `h2`/`h3` colors via its own
  `@media screen` block; that page-local CSS wins over the global stylesheet
  via specificity ordering, so its assertions are preserved.
- `page_with_pseudo_elements.html`'s own `<style>` rules (`h1::first-letter`,
  `abbr::before`) are unique selectors not touched by the global stylesheet.

If tests on any styled page fail, we fix the tests rather than weaken the
styling — per user direction.

## Russian-language pages

Pages currently declared `lang="ru"` (`page_with_alerts.html`,
`page_with_jquery.html`) are styled like everything else, **and** their
`<html lang="ru">` is changed to `<html lang="en">`. A `meta
http-equiv="Content-Language" content="ru"` on those pages is also removed if
it's there. (No integration test asserts on `lang` or `Content-Language`.)

## Risk and verification

The riskiest test categories are:

- Screenshot tests — most are protected by the skip list, but any styled page
  used as a screenshot fixture will need to be added.
- Computed-style tests (`getCssValue`) — we change link color, button
  background, font, body padding. Tests that lock in old values will fail.
- Coordinate tests — body padding shifts (x,y) by 24/16 pixels. Tests using
  relative offsets are fine.

Verification (in order):

1. `./gradlew check` — unit tests (no browser).
2. `./gradlew chrome_headless` — full integration suite.
3. `./gradlew javadocForSite` — required by `CLAUDE.md`.
4. Spot-check a handful of pages visually in a browser.

Failure handling: by default add the failing page to the skip list. Only
adjust the global CSS if multiple pages fail for the same generic reason.

## Implementation outline

Order of work for the implementation plan:

1. Write `selenide-test.css`.
2. Apply `<link>` + banner to ~3 representative pages
   (`page_with_alerts.html`, `start_page.html`, `autocomplete.html`); load each
   in a browser and eyeball.
3. Bulk-apply to all remaining HTML files in `src/test/resources/` except the
   skip list.
4. Switch `lang="ru"` → `lang="en"` on the two ru pages.
5. Run `./gradlew check` then `./gradlew chrome_headless`.
6. Triage failures: skip the offending page, or refine CSS if multiple pages
   share the same root cause.
7. Run `./gradlew javadocForSite`.

## Acceptance criteria

- All HTML test pages outside the skip list show: Selenide blue banner,
  Inter font, blue buttons, padded background.
- The skip-listed pages render byte-identically to before this change.
- `./gradlew check` passes.
- `./gradlew chrome_headless` passes (after any necessary skip-list
  additions).
- `./gradlew javadocForSite` passes.
- No JavaScript or HTML structural changes other than the added `<link>`,
  banner div, and the `lang` swap on the two ru pages.
