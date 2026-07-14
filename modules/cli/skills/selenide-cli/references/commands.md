# selenide-cli command reference

Each command is a separate invocation: `selenide [-s <session>] <command> [args]`. It connects to
the session's daemon, runs one command, prints the result, and exits. Command names are
case-insensitive. Quote any selector/value containing spaces so the shell passes one token.

**Recorded** actions append one statement to the generated code (kept only if the command
succeeds). **Meta** and **lifecycle** commands are never recorded.

## Lifecycle

| Command | Effect |
|---|---|
| `open [options] <url>` | start (or reuse) the session's daemon + browser and navigate to `<url>` |
| `close` | close the browser and stop the daemon |
| `list` | list sessions and whether each is `running` or `stale` |
| `close-all` | close every session |

The first `open` for a session spawns a detached daemon JVM (browser held open); later invocations
reuse it. `open` options (`--browser=`, `--headless`, …) apply at spawn time — see
[configuration.md](configuration.md).

## Recorded actions

| Command | Runs | Generated Java |
|---|---|---|
| `open <url>` | navigate | `open("<url>");` |
| `click <sel>` | `$(sel).click()` | `$(...).click();` |
| `setValue <sel> <text>` (alias `type`) | `$(sel).setValue(text)` | `$(...).setValue("...");` |
| `append <sel> <text>` | `$(sel).append(text)` | `$(...).append("...");` |
| `clear <sel>` | `$(sel).clear()` | `$(...).clear();` |
| `selectOption <sel> <text>` | `$(sel).selectOption(text)` | `$(...).selectOption("...");` |
| `selectRadio <sel> <value>` | `$(sel).selectRadio(value)` | `$(...).selectRadio("...");` |
| `check <sel>` / `uncheck <sel>` | `$(sel).setSelected(b)` | `$(...).setSelected(true\|false);` |
| `setSelected <sel> <true\|false>` | `$(sel).setSelected(b)` | `$(...).setSelected(true);` |
| `hover <sel>` | `$(sel).hover()` | `$(...).hover();` |
| `doubleClick <sel>` | `$(sel).doubleClick()` | `$(...).doubleClick();` |
| `contextClick <sel>` | `$(sel).contextClick()` | `$(...).contextClick();` |
| `scrollTo <sel>` | `$(sel).scrollTo()` | `$(...).scrollTo();` |
| `pressEnter\|pressTab\|pressEscape <sel>` | `$(sel).pressEnter()` … | `$(...).pressEnter();` |
| `should <sel> <cond> [value]` | `$(sel).should(cond)` | `$(...).shouldBe(...);` / `$(...).shouldHave(...);` |
| `back` / `forward` / `refresh` | navigate | `back();` / `forward();` / `refresh();` |
| `screenshot [name]` | save PNG (to `reports-folder`) | `screenshot("<name>");` (default `screenshot`) |

Notes:
- A failed command (bad selector, failed assertion) prints an error to stderr, exits non-zero, and
  is **not** recorded — retry without polluting the code.
- Multi-word values arrive as separate argv tokens and are re-joined with single spaces, so
  `setValue "#q" hello world` and `setValue "#q" "hello world"` both record `setValue("hello world")`.

## `should` conditions

| Keyword(s) | Generated | Value |
|---|---|---|
| `visible`, `hidden`, `enabled`, `disabled`, `selected`, `checked`, `editable`, `readonly`, `empty`, `focused` | `shouldBe(<cond>)` | — |
| `exist`, `disappear` | `should(<cond>)` | — |
| `text`, `exactText`, `value`, `exactValue`, `cssClass`, `matchText` | `shouldHave(<cond>("value"))` | required |
| `attribute <name>` / `attribute <name> <value>` | `shouldHave(attribute("name"[, "value"]))` | name (+ optional value) |

```bash
selenide should "#banner" visible
selenide should "#spinner" disappear
selenide should "#title" text "Welcome back"
selenide should "#price" matchText "\d+\.\d{2}"
selenide should "#link" attribute href /home
```

## Codegen / meta (not recorded)

| Command | Effect |
|---|---|
| `code` | print the generated Selenide code so far |
| `save <file>` | write the generated code to `<file>` (on the daemon host) |
| `undo` | remove the most recently recorded step |
| `reset` | clear the whole recording |

## Sessions

`-s <name>` (or `--session <name>`) selects an isolated daemon/browser; the default session is
`default`. State files live in `~/.selenide-cli/` (`<session>.port`, `<session>.log`).

```bash
selenide -s checkout open https://shop.example.com/cart
selenide -s checkout click "#pay"
selenide list
selenide -s checkout close
```
