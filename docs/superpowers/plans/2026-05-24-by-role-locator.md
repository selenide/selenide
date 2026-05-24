# byRole Locator Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `Selectors.byRole(role[, accessibleName[, options]])` locator that finds elements by their ARIA role (explicit `role="..."` or implicit from HTML semantics), optionally constrained by an accessible name.

**Architecture:** New `ByRole` class extends `OptimizedBy`, mirrors the existing `ByLabel` pattern: a small Java holder that delegates to `find-elements-by-role.js` for DOM traversal. JS computes effective role per element (explicit first, then implicit-role table) and, when an accessible name is supplied, computes a pragmatic accessible name (`aria-labelledby` → `aria-label` → wrapping/`for` label → `alt` → `textContent` → `title`) and matches it via `texts.js` helpers using the user's `TextMatchOptions`.

**Tech Stack:** Java 17, Selenium 4.44.0, JUnit Jupiter 6, AssertJ, Gradle 9.5.1. No new dependencies.

**Spec:** `docs/superpowers/specs/2026-05-24-by-role-locator-design.md`

---

## File Structure

| File | Action | Purpose |
|---|---|---|
| `src/main/java/com/codeborne/selenide/selector/ByRole.java` | Create | Java `By` subclass; carries role + optional accessible name + options; delegates to JS. |
| `src/main/resources/find-elements-by-role.js` | Create | JS helper: role resolution + accessible name + match. |
| `src/main/java/com/codeborne/selenide/Selectors.java` | Modify | Add three `byRole(...)` overloads + import. |
| `src/test/java/com/codeborne/selenide/SelectorsTest.java` | Modify | Unit tests for the three overloads (`toString` shape). |
| `src/test/resources/page_with_aria_roles.html` | Create | HTML fixture used by integration tests. |
| `src/test/java/integration/ByRoleTest.java` | Create | Integration tests covering implicit/explicit roles + accessible name + options. |

---

## Task 1: Add HTML fixture

**Files:**
- Create: `src/test/resources/page_with_aria_roles.html`

- [ ] **Step 1: Create the fixture page**

Create `src/test/resources/page_with_aria_roles.html` with this exact content:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Test page :: ARIA roles</title>
  <meta charset="UTF-8">
</head>
<body>
<h1>ARIA roles test page</h1>

<!-- Implicit roles -->
<button id="implicit-button">Submit</button>
<a href="#x" id="implicit-link">Documentation</a>
<input type="checkbox" id="implicit-checkbox">
<input type="radio" id="implicit-radio">
<input type="text" id="implicit-textbox" name="username">
<input type="search" id="implicit-searchbox">
<textarea id="implicit-textarea"></textarea>
<select id="implicit-combobox"><option>One</option></select>
<select id="implicit-listbox" multiple><option>A</option><option>B</option></select>

<h2 id="implicit-heading-2">Section heading</h2>
<h3 id="implicit-heading-3">Sub heading</h3>

<img src="data:," alt="A friendly cat" id="implicit-img">

<ul id="implicit-list">
  <li id="implicit-listitem-1">First</li>
  <li>Second</li>
</ul>

<nav id="implicit-nav">Site links</nav>
<main id="implicit-main">Main content</main>

<table id="implicit-table">
  <tr id="implicit-row">
    <th scope="col" id="implicit-columnheader">Name</th>
    <th scope="row" id="implicit-rowheader">Age</th>
  </tr>
  <tr>
    <td id="implicit-cell">Alice</td>
    <td>30</td>
  </tr>
</table>

<!-- Explicit roles -->
<div role="button" id="explicit-button">Custom button</div>
<div role="alert" id="explicit-alert">Heads up!</div>

<!-- Accessible name variations -->
<button id="name-text">Save</button>
<button aria-label="Close dialog" id="name-aria-label">&times;</button>
<span id="lbl-profile">Profile</span>
<button aria-labelledby="lbl-profile" id="name-aria-labelledby">&#9881;</button>

<label>Username
  <input type="text" id="name-wrapping-label">
</label>

<label for="email-field">Email address</label>
<input type="text" id="email-field">

<button title="Help" id="name-title-fallback"></button>

<!-- Explicit role overrides implicit -->
<button role="link" id="explicit-overrides-implicit">Looks like a button but role=link</button>

<!-- Multiple buttons for partial / case-insensitive checks -->
<button id="cancel-btn">Cancel</button>
<button id="confirm-btn">Confirm</button>
</body>
</html>
```

- [ ] **Step 2: Verify file is present**

Run: `ls src/test/resources/page_with_aria_roles.html`
Expected: file listed without error.

- [ ] **Step 3: Commit**

```bash
git add src/test/resources/page_with_aria_roles.html
git commit -m "add HTML fixture for byRole tests"
```

---

## Task 2: Create the `ByRole` Java class with `toString` only (no JS yet)

**Files:**
- Create: `src/main/java/com/codeborne/selenide/selector/ByRole.java`
- Modify: `src/test/java/com/codeborne/selenide/SelectorsTest.java`

We start with `toString`-only behavior so the unit tests pass without needing the JS layer or a browser.

- [ ] **Step 1: Write failing unit tests**

Open `src/test/java/com/codeborne/selenide/SelectorsTest.java`. Add the following imports at the top if missing (keep alphabetical order):

```java
import com.codeborne.selenide.selector.ByRole;
import static com.codeborne.selenide.TextMatchOptions.partialText;
```

Then add these three test methods to the existing `SelectorsTest` class (anywhere among the existing tests):

```java
  @Test
  void byRole_withoutName_describesItself() {
    By selector = Selectors.byRole("button");
    assertThat(selector).isInstanceOf(ByRole.class);
    assertThat(selector).hasToString("by role \"button\"");
  }

  @Test
  void byRole_withName_describesItself() {
    By selector = Selectors.byRole("button", "Submit");
    assertThat(selector).isInstanceOf(ByRole.class);
    assertThat(selector).hasToString("by role \"button\" with name \"Submit\" (full text, case sensitive)");
  }

  @Test
  void byRole_withNameAndOptions_describesItself() {
    By selector = Selectors.byRole("button", "submit", partialText().caseInsensitive());
    assertThat(selector).isInstanceOf(ByRole.class);
    assertThat(selector).hasToString("by role \"button\" with name \"submit\" (partial text, case insensitive)");
  }
```

- [ ] **Step 2: Run tests, verify they fail**

Run: `./gradlew check --tests com.codeborne.selenide.SelectorsTest`
Expected: compile error — `Selectors.byRole(...)` and `ByRole` do not exist yet.

- [ ] **Step 3: Create `ByRole` class**

Create `src/main/java/com/codeborne/selenide/selector/ByRole.java` with:

```java
package com.codeborne.selenide.selector;

import com.codeborne.selenide.TextMatchOptions;
import com.codeborne.selenide.impl.JavaScript;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ByRole extends OptimizedBy {
  private static final JavaScript js = new JavaScript("find-elements-by-role.js");
  private final String role;
  @Nullable
  private final String accessibleName;
  private final TextMatchOptions options;

  public ByRole(String role) {
    this(role, null, TextMatchOptions.fullText());
  }

  public ByRole(String role, String accessibleName) {
    this(role, accessibleName, TextMatchOptions.fullText());
  }

  public ByRole(String role, @Nullable String accessibleName, TextMatchOptions options) {
    this.role = role;
    this.accessibleName = accessibleName;
    this.options = options;
  }

  @Override
  protected List<WebElement> findElements(SearchContext context, int limit) {
    return requireNonNull(js.execute(context, js.node(context), role, accessibleName, options.toMap(), limit));
  }

  @Override
  public String toString() {
    return accessibleName == null
      ? "by role \"%s\"".formatted(role)
      : "by role \"%s\" with name \"%s\" (%s)".formatted(role, accessibleName, options);
  }
}
```

- [ ] **Step 4: Add `Selectors.byRole(...)` overloads**

Open `src/main/java/com/codeborne/selenide/Selectors.java`. Add the import (preserve alphabetical order in the existing import block):

```java
import com.codeborne.selenide.selector.ByRole;
```

Add these three methods near the other `by*` methods (right after `byTestId` is a good spot):

```java
  /**
   * Find element by its <a href="https://www.w3.org/TR/wai-aria-1.2/#roles">ARIA role</a>.
   * Matches both explicit {@code role="..."} attributes and common implicit roles
   * (e.g. {@code <button>} ⇒ role {@code button}, {@code <h1>..<h6>} ⇒ role {@code heading}).
   *
   * @param role ARIA role name (e.g. "button", "link", "heading", "checkbox")
   * @since 7.17.0
   */
  public static By byRole(String role) {
    return new ByRole(role);
  }

  /**
   * Find element by ARIA role with the given accessible name.
   * The accessible name is computed pragmatically:
   * {@code aria-labelledby} → {@code aria-label} → associated {@code <label>} →
   * {@code alt} (for {@code <img>}) → {@code textContent} → {@code title}.
   *
   * @param role ARIA role name
   * @param accessibleName expected <a href="https://w3c.github.io/accname/#dfn-accessible-name">accessible name</a>
   * @since 7.17.0
   */
  public static By byRole(String role, String accessibleName) {
    return new ByRole(role, accessibleName);
  }

  /**
   * Find element by ARIA role with the given accessible name, using the provided
   * text-match options (case sensitivity, whitespace handling, full or partial match).
   *
   * @since 7.17.0
   */
  public static By byRole(String role, String accessibleName, TextMatchOptions options) {
    return new ByRole(role, accessibleName, options);
  }
```

- [ ] **Step 5: Run unit tests, verify they pass**

Run: `./gradlew :modules:core:test --tests com.codeborne.selenide.SelectorsTest`
Expected: all three new tests pass + existing tests still pass.

- [ ] **Step 6: Run full `check` to catch checkstyle / spotbugs issues**

Run: `./gradlew check`
Expected: BUILD SUCCESSFUL.

If there's a checkstyle warning about ordering of imports in `Selectors.java`, sort them alphabetically (the file already follows alphabetical order — pay attention to where `com.codeborne.selenide.selector.ByRole` slots in among the other `selector.By*` imports).

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/codeborne/selenide/selector/ByRole.java \
        src/main/java/com/codeborne/selenide/Selectors.java \
        src/test/java/com/codeborne/selenide/SelectorsTest.java
git commit -m "add byRole locator (Java API + toString)"
```

Note: at this point the JS file does not exist, so `findElements` will fail at runtime. Unit tests don't exercise it; integration tests in later tasks will.

---

## Task 3: JS helper — role resolution (explicit + implicit table)

**Files:**
- Create: `src/main/resources/find-elements-by-role.js`
- Create: `src/test/java/integration/ByRoleTest.java`

This task creates the JS helper and the first batch of integration tests that exercise role-matching only (no accessible name).

- [ ] **Step 1: Write failing integration test (role-only)**

Create `src/test/java/integration/ByRoleTest.java` with:

```java
package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byRole;

final class ByRoleTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_aria_roles.html");
  }

  @Test
  void findsElementByImplicitButtonRole() {
    $(byRole("button")).shouldHave(attribute("id", "implicit-button"));
  }

  @Test
  void findsElementByImplicitLinkRole() {
    $(byRole("link")).shouldHave(attribute("id", "implicit-link"));
  }

  @Test
  void findsElementByImplicitHeadingRole() {
    $$(byRole("heading")).shouldHave(size(3));
  }

  @Test
  void findsElementByImplicitCheckboxRole() {
    $(byRole("checkbox")).shouldHave(attribute("id", "implicit-checkbox"));
  }

  @Test
  void findsElementByImplicitRadioRole() {
    $(byRole("radio")).shouldHave(attribute("id", "implicit-radio"));
  }

  @Test
  void findsElementByImplicitTextboxRole() {
    $(byRole("textbox")).shouldHave(attribute("id", "implicit-textbox"));
  }

  @Test
  void findsElementByImplicitSearchboxRole() {
    $(byRole("searchbox")).shouldHave(attribute("id", "implicit-searchbox"));
  }

  @Test
  void findsElementByImplicitComboboxRole() {
    $(byRole("combobox")).shouldHave(attribute("id", "implicit-combobox"));
  }

  @Test
  void findsElementByImplicitListboxRole() {
    $(byRole("listbox")).shouldHave(attribute("id", "implicit-listbox"));
  }

  @Test
  void findsElementByImplicitImgRole() {
    $(byRole("img")).shouldHave(attribute("id", "implicit-img"));
  }

  @Test
  void findsElementByImplicitListRole() {
    $(byRole("list")).shouldHave(attribute("id", "implicit-list"));
  }

  @Test
  void findsElementByImplicitListitemRole() {
    $$(byRole("listitem")).shouldHave(size(2));
  }

  @Test
  void findsElementByImplicitNavigationRole() {
    $(byRole("navigation")).shouldHave(attribute("id", "implicit-nav"));
  }

  @Test
  void findsElementByImplicitMainRole() {
    $(byRole("main")).shouldHave(attribute("id", "implicit-main"));
  }

  @Test
  void findsElementByImplicitTableRole() {
    $(byRole("table")).shouldHave(attribute("id", "implicit-table"));
  }

  @Test
  void findsElementByImplicitColumnheaderRole() {
    $(byRole("columnheader")).shouldHave(attribute("id", "implicit-columnheader"));
  }

  @Test
  void findsElementByImplicitRowheaderRole() {
    $(byRole("rowheader")).shouldHave(attribute("id", "implicit-rowheader"));
  }

  @Test
  void findsElementByImplicitCellRole() {
    $(byRole("cell")).shouldHave(text("Alice"));
  }

  @Test
  void findsElementByExplicitRole() {
    $(byRole("alert")).shouldHave(attribute("id", "explicit-alert"));
  }

  @Test
  void explicitRoleOverridesImplicit() {
    $$(byRole("link")).shouldHave(size(2));
    $(byRole("link")).shouldHave(attribute("id", "implicit-link"));
  }
}
```

- [ ] **Step 2: Run tests, verify they fail**

Run: `./gradlew chrome_headless --tests integration.ByRoleTest`
Expected: tests fail — `find-elements-by-role.js` is missing, the JS execution throws.

- [ ] **Step 3: Create the JS helper**

Create `src/main/resources/find-elements-by-role.js` with:

```js
(function () {
  import 'texts.js'

  const root = arguments[0] || document.body;
  const expectedRole = arguments[1];
  const expectedName = arguments[2];
  const options = arguments[3];
  const limit = arguments[4];

  const IMPLICIT_ROLES = [
    { role: 'button',       match: el => el.tagName === 'BUTTON' },
    { role: 'link',         match: el => (el.tagName === 'A' || el.tagName === 'AREA') && el.hasAttribute('href') },
    { role: 'checkbox',     match: el => el.tagName === 'INPUT' && el.type === 'checkbox' },
    { role: 'radio',        match: el => el.tagName === 'INPUT' && el.type === 'radio' },
    { role: 'searchbox',    match: el => el.tagName === 'INPUT' && el.type === 'search' },
    { role: 'textbox',      match: el => (el.tagName === 'INPUT' &&
                                          ['text','email','tel','url','password',''].includes(el.type || '')) ||
                                          el.tagName === 'TEXTAREA' },
    { role: 'listbox',      match: el => el.tagName === 'SELECT' && (el.multiple || el.size > 1) },
    { role: 'combobox',     match: el => el.tagName === 'SELECT' },
    { role: 'option',       match: el => el.tagName === 'OPTION' },
    { role: 'heading',      match: el => /^H[1-6]$/.test(el.tagName) },
    { role: 'img',          match: el => el.tagName === 'IMG' && el.hasAttribute('alt') },
    { role: 'list',         match: el => el.tagName === 'UL' || el.tagName === 'OL' },
    { role: 'listitem',     match: el => el.tagName === 'LI' },
    { role: 'table',        match: el => el.tagName === 'TABLE' },
    { role: 'row',          match: el => el.tagName === 'TR' },
    { role: 'columnheader', match: el => el.tagName === 'TH' && el.getAttribute('scope') === 'col' },
    { role: 'rowheader',    match: el => el.tagName === 'TH' &&
                                          (el.getAttribute('scope') === 'row' || !el.hasAttribute('scope')) },
    { role: 'cell',         match: el => el.tagName === 'TD' },
    { role: 'navigation',   match: el => el.tagName === 'NAV' },
    { role: 'main',         match: el => el.tagName === 'MAIN' },
    { role: 'banner',       match: el => el.tagName === 'HEADER' && !el.closest('article, section, aside, nav') },
    { role: 'contentinfo',  match: el => el.tagName === 'FOOTER' && !el.closest('article, section, aside, nav') },
    { role: 'dialog',       match: el => el.tagName === 'DIALOG' },
    { role: 'form',         match: el => el.tagName === 'FORM' &&
                                          (el.hasAttribute('aria-label') ||
                                           el.hasAttribute('aria-labelledby') ||
                                           el.hasAttribute('name')) }
  ];

  function effectiveRole(el) {
    const explicit = (el.getAttribute('role') || '').trim().split(/\s+/)[0];
    if (explicit) return explicit;
    for (const entry of IMPLICIT_ROLES) {
      if (entry.match(el)) return entry.role;
    }
    return null;
  }

  function accessibleName(el) {
    const ariaLabelledBy = el.getAttribute('aria-labelledby');
    if (ariaLabelledBy) {
      const text = ariaLabelledBy.split(/\s+/)
        .map(id => document.getElementById(id))
        .filter(node => !!node)
        .map(node => node.textContent || '')
        .join(' ').trim();
      if (text) return text;
    }
    const ariaLabel = el.getAttribute('aria-label');
    if (ariaLabel && ariaLabel.trim()) return ariaLabel;

    const tag = el.tagName;
    if (tag === 'INPUT' || tag === 'SELECT' || tag === 'TEXTAREA') {
      const wrapping = el.closest('label');
      if (wrapping && wrapping.textContent.trim()) return wrapping.textContent;
      if (el.id) {
        const labelled = document.querySelector('label[for="' + CSS.escape(el.id) + '"]');
        if (labelled && labelled.textContent.trim()) return labelled.textContent;
      }
    }
    if (tag === 'IMG') {
      const alt = el.getAttribute('alt');
      if (alt) return alt;
    }
    const text = (el.textContent || '').trim();
    if (text) return text;
    const title = el.getAttribute('title');
    if (title) return title;
    return '';
  }

  function nameMatches(el) {
    if (expectedName == null) return true;
    const actual = normalizeText(accessibleName(el), options);
    const expected = normalizeText(expectedName, options);
    return textMatches(options, actual, expected);
  }

  const result = [];
  const candidates = root.querySelectorAll('*');
  for (let i = 0; i < candidates.length && result.length < limit; i++) {
    const el = candidates[i];
    if (effectiveRole(el) === expectedRole && nameMatches(el)) {
      result.push(el);
    }
  }
  return result;
})(...arguments)
```

- [ ] **Step 4: Run integration tests, verify they pass**

Run: `./gradlew chrome_headless --tests integration.ByRoleTest`
Expected: all 21 tests in `ByRoleTest` pass.

If anything fails, **don't blanket-fix the JS** — look at the failing case and confirm whether the fixture's expectation was wrong or the JS is wrong, then adjust the smaller piece.

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/find-elements-by-role.js \
        src/test/java/integration/ByRoleTest.java
git commit -m "implement byRole locator (role resolution + accessible name)"
```

---

## Task 4: Integration tests — accessible name + match options

**Files:**
- Modify: `src/test/java/integration/ByRoleTest.java`

The JS file already supports accessible-name matching (added in Task 3 to avoid splitting the JS into two halves). This task drives that behavior with explicit tests.

- [ ] **Step 1: Add accessible-name integration tests**

Append the following test methods to the `ByRoleTest` class (before the closing brace). Also add these imports (preserve alphabetical order):

```java
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;
```

And the new tests:

```java
  @Test
  void matchesAccessibleNameFromTextContent() {
    $(byRole("button", "Save")).shouldHave(attribute("id", "name-text"));
  }

  @Test
  void matchesAccessibleNameFromAriaLabel() {
    $(byRole("button", "Close dialog")).shouldHave(attribute("id", "name-aria-label"));
  }

  @Test
  void matchesAccessibleNameFromAriaLabelledby() {
    $(byRole("button", "Profile")).shouldHave(attribute("id", "name-aria-labelledby"));
  }

  @Test
  void matchesAccessibleNameFromWrappingLabel() {
    $(byRole("textbox", "Username")).shouldHave(attribute("id", "name-wrapping-label"));
  }

  @Test
  void matchesAccessibleNameFromLabelFor() {
    $(byRole("textbox", "Email address")).shouldHave(attribute("id", "email-field"));
  }

  @Test
  void matchesAccessibleNameFromImgAlt() {
    $(byRole("img", "A friendly cat")).shouldHave(attribute("id", "implicit-img"));
  }

  @Test
  void matchesAccessibleNameFromTitleFallback() {
    $(byRole("button", "Help")).shouldHave(attribute("id", "name-title-fallback"));
  }

  @Test
  void noNameMatch_doesNotFindElement() {
    $(byRole("button", "Nonexistent")).shouldNot(exist);
  }

  @Test
  void matchesAccessibleNameWithPartialOption() {
    $(byRole("button", "ave", partialText())).shouldHave(attribute("id", "name-text"));
  }

  @Test
  void matchesAccessibleNameCaseInsensitively() {
    $(byRole("button", "SUBMIT", fullText().caseInsensitive()))
      .shouldHave(attribute("id", "implicit-button"));
  }

  @Test
  void matchesAccessibleNamePartialAndCaseInsensitively() {
    // Only "Confirm" contains "con" (case-insensitive) among button accessible names in the fixture.
    $$(byRole("button", "con", partialText().caseInsensitive()))
      .shouldHave(size(1));
  }

  @Test
  void multipleButtonsByRoleOnly() {
    // Implicit + explicit role=button on the page: implicit-button, name-text, name-aria-label,
    // name-aria-labelledby, name-title-fallback, cancel-btn, confirm-btn, explicit-button = 8
    $$(byRole("button")).shouldHave(size(8));
  }
```

About the `matchesAccessibleNamePartialAndCaseInsensitively` count: after this test runs, look at the fixture and confirm exactly which buttons contain "con" (case-insensitive) in their accessible name. The expected matches are `Confirm` and `Close dialog` (both contain "co" but only those two contain "con" — wait, `Close dialog` contains `clo`, not `con`). Re-check before running. If the count is wrong, **fix the assertion to match what the fixture actually produces — don't change the JS**.

Note on `multipleButtonsByRoleOnly`: count buttons in the fixture before running. Buttons with `role="button"` implicit or explicit: `implicit-button`, `name-text`, `name-aria-label`, `name-aria-labelledby`, `name-title-fallback`, `cancel-btn`, `confirm-btn`, `explicit-button`. That's 8. Verify after running.

- [ ] **Step 2: Run integration tests, verify they pass**

Run: `./gradlew chrome_headless --tests integration.ByRoleTest`
Expected: all tests pass. If a count is wrong, adjust the assertion to reflect what the fixture actually contains — don't tweak the JS to game a count.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/integration/ByRoleTest.java
git commit -m "add byRole tests for accessible name matching and options"
```

---

## Task 5: Full verification

**Files:** no source changes; this is a verification gate.

- [ ] **Step 1: Run full unit-test suite**

Run: `./gradlew check`
Expected: BUILD SUCCESSFUL, no checkstyle / spotbugs warnings.

- [ ] **Step 2: Run Javadoc-for-site check** (required by CLAUDE.md before pushing)

Run: `./gradlew javadocForSite`
Expected: BUILD SUCCESSFUL.

If broken, the most likely cause is a malformed Javadoc tag in the new `byRole` overloads — fix it and re-run.

- [ ] **Step 3: Run Chrome headless integration suite**

Run: `./gradlew chrome_headless --tests integration.ByRoleTest`
Expected: all `ByRoleTest` tests pass.

- [ ] **Step 4: Run the broader integration smoke**

Run: `./gradlew chrome_headless --tests integration.SelectorsTest`
Expected: no regressions in the existing selector tests.

- [ ] **Step 5: Verify git state is clean**

Run: `git status`
Expected: working tree clean, branch ahead of `main` by 5 commits (spec + fixture + Java/unit + JS+integration + name tests).

---

## Self-Review Notes

- **Spec coverage:**
  - Public API: Task 2 (three overloads).
  - Architecture (ByRole + JS helper): Task 2 + Task 3.
  - Implicit role table: Task 3 (every row in the design's table is in `IMPLICIT_ROLES`).
  - Accessible name pragmatic subset: Task 3 JS (`accessibleName` function follows the spec order).
  - Tests: Tasks 2 (unit), 3 + 4 (integration).
  - Files-changed list in spec matches the file table above.

- **Out of scope (deferred per spec):** ARIA states (checked / pressed / expanded / level / etc.) and full W3C AccName algorithm are intentionally not implemented.

- **Type consistency:** `ByRole` constructor signatures stay consistent between Task 2 (definition) and the test in Task 2 (instantiation). JS argument order in Task 3 (`node, role, name, options, limit`) matches the Java call site in `ByRole.findElements`.

- **TextMatchOptions:** `toString` for `partialText().caseInsensitive()` evaluates to `"partial text, case insensitive"` (whitespace handling omitted because it defaults to TRIM_WHITESPACES). Unit-test expectations in Task 2 reflect that.

