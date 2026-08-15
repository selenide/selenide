# Generating Selenide tests (codegen)

The daemon records every successful command across invocations and can emit runnable Selenide Java —
turning an exploratory session into a test.

## How recording works

- The daemon registers a `LogEventListener` with Selenide's `SelenideLogger`. Every command run
  through the Selenide API fires a log event.
- A statement is kept **only when its command completed successfully** (event status `PASS`).
  Typos, missing elements, and failed assertions are reported (stderr, non-zero exit) but not
  recorded.
- Recording is **daemon-side and cumulative**: each separate `selenide <cmd>` invocation adds to the
  same recording held by the session's daemon. `selenide code` returns the whole thing.
- It captures commands run **through the CLI**, not manual mouse clicks in the browser window.

## Getting the code out

```bash
selenide code                 # print to stdout
selenide save Login.java      # daemon writes the file (path is relative to the daemon's cwd)
```

The snippet has the required static imports followed by the statements:

```java
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

open("https://example.com/login");
$("#email").setValue("user@example.com");
$("#password").setValue("secret");
$("button[type=submit]").click();
$(byText("Welcome")).shouldBe(visible);
```

## Turning the snippet into a test class

```java
package com.example;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class LoginTest {
  @Test
  void login() {
    open("https://example.com/login");
    $("#email").setValue("user@example.com");
    $("#password").setValue("secret");
    $("button[type=submit]").click();
    $(byText("Welcome")).shouldBe(visible);
  }
}
```

Add the `com.codeborne:selenide` dependency and run with your build tool; Selenium Manager fetches
the driver automatically.

## Assertions are recorded

`should ...` commands are part of the recording — run them as you go so the generated test verifies
behaviour, not just actions:

```bash
selenide should "#flash" text "You logged into a secure area!"
selenide should "#logout" visible
```

## Best practices

1. **Prefer stable selectors** (`id`/`data-testid` via CSS, or `text=`/`byText` for labels). See
   [selectors.md](selectors.md). `byText(...)` is readable but brittle under i18n.
2. **Assert as you go** so the test is meaningful.
3. **Use `undo`/`reset`** to keep the recording clean after a mistake.
4. **Re-run headless** once the flow works, then `selenide code` for the final snippet.
5. **Recording resets with the session** — `close` (or `close-all`) discards it; grab `code`/`save`
   before closing.
