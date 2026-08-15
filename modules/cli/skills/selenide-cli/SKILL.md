---
name: selenide-cli
description: Automate a browser and generate Selenide Java via a stateless, per-invocation CLI over a background daemon.
allowed-tools: Bash(selenide:*) Bash(java:*) Bash(./gradlew:*)
---

# Browser automation & codegen with selenide-cli

`selenide-cli` is a **stateless, per-invocation CLI over a background daemon** (same model as
`playwright-cli`). `selenide open <url>` starts (or reuses) a daemon that holds a live browser for
the session; each later invocation (`selenide click "#submit"`, `selenide code`, `selenide close`)
is its own process that connects to the daemon, runs one command, prints the result, and exits.
Successful commands are recorded (daemon-side, via `SelenideLogger`) and accumulate into runnable
**Selenide Java**, printed by `selenide code`.

## Quick start

```bash
# open starts the daemon + browser and navigates
selenide open https://playwright.dev
# interact using selectors (CSS by default)
selenide click "text=Get started"
selenide setValue "#search" "page.click"
selenide press "#search" Enter          # (pressEnter) — see command list
# print the generated Selenide Java so far
selenide code
# stop the browser + daemon
selenide close
```

If a `selenide` launcher is not on your PATH, use `java -jar modules/cli/build/libs/selenide-cli-*.jar`
(build it once with `./gradlew :modules:cli:shadowJar`). See
[references/build-and-run.md](references/build-and-run.md).

## Commands

### Lifecycle

```bash
selenide open https://example.com/          # start/reuse daemon + navigate
selenide open --headless --browser=firefox https://example.com/
selenide close                              # close browser + stop daemon
selenide list                               # list sessions (running / stale)
selenide close-all                          # close every session
```

### Actions (recorded)

```bash
selenide click "#submit"
selenide setValue "#email" "user@example.com"     # alias: type
selenide append "#notes" " more text"
selenide clear "#email"
selenide selectOption "#country" "Estonia"
selenide selectRadio "#gender" male
selenide check "#agree"                            # uncheck / setSelected <sel> <bool>
selenide hover "#menu"
selenide doubleClick "#cell"                        # contextClick, scrollTo
selenide pressEnter "#search"                       # pressTab, pressEscape
selenide back                                       # forward, refresh
selenide screenshot                                 # screenshot <name>
```

### Assertions

```bash
selenide should "#msg" visible                      # shouldBe(visible)
selenide should "#msg" text "Welcome home"          # shouldHave(text("Welcome home"))
selenide should "#inp" value 42                      # shouldHave(value("42"))
selenide should "#link" attribute href /home         # shouldHave(attribute("href", "/home"))
```

Conditions: `visible`, `hidden`, `exist`, `disappear`, `enabled`, `disabled`, `selected`, `checked`,
`editable`, `readonly`, `empty`, `focused`, `text`, `exactText`, `value`, `exactValue`, `cssClass`,
`matchText`, `attribute <name> [value]`.

### Codegen (not recorded)

```bash
selenide code                # print generated Selenide Java so far
selenide save Login.java     # write it to a file
selenide undo                # drop the last recorded step
selenide reset               # clear the recording
```

## Targeting elements

Selectors are ordinary CLI arguments — **quote** any that contain spaces so the shell passes a
single token. CSS is the default.

```bash
selenide click "#main > button.submit"     # CSS
selenide click "text=Sign In"              # -> $(byText("Sign In"))
selenide click "xpath=//button[@id='go']"  # -> $(byXpath(...))
selenide click "//a[normalize-space()='Home']"  # leading // = xpath
```

More: [references/selectors.md](references/selectors.md).

## Sessions

Run isolated browsers concurrently with `-s <name>` (default: `default`). State lives in
`~/.selenide-cli/`.

```bash
selenide -s auth   open https://app.example.com/login
selenide -s public open https://example.com
selenide -s auth   setValue "#user" alice
selenide list
selenide -s auth   close
selenide close-all
```

## `open` options

```bash
selenide open --browser=chrome https://example.com     # chrome (default), firefox, edge, safari
selenide open --headless https://example.com
selenide open --browser-size=1920x1080 https://example.com
selenide open --base-url=https://example.com /login    # relative path resolved against base-url
selenide open --remote=http://localhost:4444/wd/hub https://example.com   # Selenium Grid
selenide --version
```

Options take effect when the daemon is first spawned for a session. Full list:
[references/configuration.md](references/configuration.md).

## Example: record a login into a Selenide test

```bash
selenide open --headless https://the-internet.herokuapp.com/login
selenide setValue "#username" tomsmith
selenide setValue "#password" "SuperSecretPassword!"
selenide click "button[type=submit]"
selenide should "text=You logged into a secure area!" visible
selenide code
selenide close
```

```java
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

open("https://the-internet.herokuapp.com/login");
$("#username").setValue("tomsmith");
$("#password").setValue("SuperSecretPassword!");
$("button[type=submit]").click();
$(byText("You logged into a secure area!")).shouldBe(visible);
```

## Notes vs. playwright-cli

Same architecture (per-invocation client + persistent daemon, named sessions, `list`/`close-all`).
Selenide CLI intentionally does **not** (yet) provide: page snapshots with element refs, tabs,
network mocking/inspection, tracing, video, or storage-state commands. Element targeting is by
selector (CSS / `text=` / `xpath=`), not by snapshot ref.

## Specific tasks

* **Full command grammar** — [references/commands.md](references/commands.md)
* **Generating tests (codegen)** — [references/codegen.md](references/codegen.md)
* **Selectors & conditions** — [references/selectors.md](references/selectors.md)
* **Browser configuration & Grid** — [references/configuration.md](references/configuration.md)
* **Build, install, sessions & daemon lifecycle** — [references/build-and-run.md](references/build-and-run.md)
* **Scripting & CI** — [references/scripting.md](references/scripting.md)
