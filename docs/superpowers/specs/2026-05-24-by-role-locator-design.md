# `byRole` locator — design

## Goal

Add a new locator `Selectors.byRole(...)` that finds elements by their
[ARIA role](https://www.w3.org/TR/wai-aria-1.2/#roles), optionally constrained by an
[accessible name](https://w3c.github.io/accname/#dfn-accessible-name). The locator must
recognise both explicit `role="..."` attributes and the most common implicit roles
assigned by HTML semantics (e.g. `<button>` ⇒ role `button`).

Inspired by Playwright's `getByRole`. Aligns with the existing family of
attribute-/text-oriented locators on `Selectors` (`byText`, `byLabel`, `byPlaceholder`,
`byTitle`, `byAltText`, `byTestId`).

## Public API

In `com.codeborne.selenide.Selectors`:

```java
/**
 * Find element by ARIA role.
 * Matches both explicit role="..." and common implicit roles
 * (e.g. <button> ⇒ "button", <a href> ⇒ "link", <h1>-<h6> ⇒ "heading").
 *
 * @param role ARIA role name (e.g. "button", "link", "heading", "checkbox")
 * @since 7.17.0
 */
public static By byRole(String role);

/**
 * Find element by ARIA role with the given accessible name (full text, case-sensitive).
 * @since 7.17.0
 */
public static By byRole(String role, String accessibleName);

/**
 * Find element by ARIA role with the given accessible name, using the provided
 * text-match options (case-sensitivity, whitespace handling, full/partial match).
 * @since 7.17.0
 */
public static By byRole(String role, String accessibleName, TextMatchOptions options);
```

The parameter is called `accessibleName` (not `text` / `label`) so the public Javadoc
uses the same vocabulary as the W3C specs the user reads when learning ARIA.

## Architecture

Mirrors the existing pattern used by `ByLabel` / `find-elements-by-label.js`:

- `com.codeborne.selenide.selector.ByRole` — new `By` subclass extending `OptimizedBy`,
  carries the `role`, an optional `accessibleName`, and `TextMatchOptions`. Loads JS
  via `new JavaScript("find-elements-by-role.js")`.
- `src/main/resources/find-elements-by-role.js` — JS helper. `imports 'texts.js'` to
  re-use `normalizeText` / `textMatches` from the existing text utilities.

`ByRole.toString()` returns:
- `by role "button"` when no name is supplied;
- `by role "button" with name "Submit" (FULL_TEXT, CASE_SENSITIVE, TRIM_WHITESPACES)`
  when a name is supplied (format consistent with `ByLabel.toString()`).

`accessibleName` is nullable internally; the no-name overload passes `null` and the JS
helper skips name matching when it receives `null`.

## JS helper — role resolution

`find-elements-by-role.js` walks the root with `querySelectorAll('*')`, then for each
element computes its effective role:

1. If the element has an explicit non-empty `role` attribute, that wins (first
   space-separated token only, per ARIA fallback role rules).
2. Otherwise, look up the implicit role from a tag-name table.

Implicit role table (the "common interactive set"):

| Selector | Implicit role |
|---|---|
| `button` | `button` |
| `a[href]`, `area[href]` | `link` |
| `input[type="checkbox"]` | `checkbox` |
| `input[type="radio"]` | `radio` |
| `input[type="text"]`, `input[type="email"]`, `input[type="tel"]`, `input[type="url"]`, `input[type="password"]`, `input` without `type`, `textarea` | `textbox` |
| `input[type="search"]` | `searchbox` |
| `select[multiple]`, `select[size>1]` | `listbox` |
| `select` (default) | `combobox` |
| `option` | `option` |
| `h1`..`h6` | `heading` |
| `img[alt]` | `img` |
| `ul`, `ol` | `list` |
| `li` | `listitem` |
| `table` | `table` |
| `tr` | `row` |
| `td` | `cell` |
| `th[scope="col"]` | `columnheader` |
| `th[scope="row"]`, `th` (default) | `rowheader` |
| `nav` | `navigation` |
| `header` (not inside `article`/`section`/`aside`/`nav`) | `banner` |
| `main` | `main` |
| `footer` (not inside `article`/`section`/`aside`/`nav`) | `contentinfo` |
| `form` with accessible name (label / `aria-label` / `aria-labelledby`) | `form` |
| `dialog` | `dialog` |

Roles outside the table are only matched via explicit `role="..."`.

## JS helper — accessible name computation (pragmatic subset)

For each candidate element, compute the accessible name as the first non-empty value of:

1. `aria-labelledby` — concatenated `textContent` of each referenced id (space-separated)
2. `aria-label` attribute
3. For form controls (`input`, `select`, `textarea`): associated `<label>` —
   wrapping `<label>` first, then `<label for="id">`
4. For `img`: `alt` attribute
5. Element's `textContent`
6. `title` attribute (final fallback)

The resulting name is normalised and compared using the user-supplied `TextMatchOptions`
via the existing `normalizeText` + `textMatches` helpers in `texts.js`. Default options
(when only `byRole(role, name)` is called) are `TextMatchOptions.fullText()` —
case-sensitive, trim-whitespaces, full-text match.

## Tests

### Unit tests — `src/test/java/com/codeborne/selenide/SelectorsTest.java`

Add cases covering `toString()` representations:

- `byRole("button")` ⇒ `by role "button"`
- `byRole("button", "Submit")` ⇒ includes `with name "Submit"` and the default options
- `byRole("button", "submit", partialText().caseInsensitive())` ⇒ reflects the supplied
  options in `toString()`.

### Integration tests — `src/test/java/integration/ByRoleTest.java`

Add `ITest`-based integration tests against a new fixture
`src/test/resources/page_with_aria_roles.html` covering:

- Explicit `role="..."` — e.g. `<div role="button">…`
- Implicit roles — `<button>`, `<a href>`, `<h1>`–`<h3>`, `<input type="checkbox">`,
  `<ul>`/`<li>`, `<nav>`, `<main>`, `<table>`/`<tr>`/`<td>`/`<th scope=…>`
- Accessible name sources — `aria-label`, `aria-labelledby`, wrapping `<label>`,
  `<label for=…>`, plain `textContent`, `<img alt=…>`, `title` fallback
- `TextMatchOptions` — `partialText()`, `caseInsensitive()` combinations
- Edge cases:
  - Element with both explicit and implicit role: explicit wins.
  - No-name overload returns the first element with the requested role.
  - Unknown role string still works for explicit `role="…"` matches.

## Out of scope (deliberately deferred)

- ARIA state filters (`aria-checked`, `aria-pressed`, `aria-expanded`,
  `aria-selected`, `aria-disabled`, heading `level`). The three-overload API leaves
  room to add a richer options object later without breaking callers.
- Full W3C AccName algorithm — no recursive subtree computation, no hidden-element
  rules, no table-caption traversal. The pragmatic subset above covers the realistic
  cases that show up in test code.
- `aria-owns`-driven role/name inheritance.
- Full HTML-AAM implicit-role table — only the "common interactive set" is wired up.
  Adding more rows is a pure data-table change later.

## Files changed

- `src/main/java/com/codeborne/selenide/Selectors.java` — three new `byRole` overloads.
- `src/main/java/com/codeborne/selenide/selector/ByRole.java` — new file.
- `src/main/resources/find-elements-by-role.js` — new file.
- `src/test/java/com/codeborne/selenide/SelectorsTest.java` — unit cases.
- `src/test/java/integration/ByRoleTest.java` — new integration test.
- `src/test/resources/page_with_aria_roles.html` — new fixture.
