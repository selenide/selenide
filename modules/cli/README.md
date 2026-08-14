# Selenide CLI

A command-line tool for Selenide, modelled on `playwright-cli`: a **stateless, per-invocation CLI
over a background daemon**.

`selenide open <url>` starts (or reuses) a background **daemon** that holds a live browser for the
session. Each subsequent invocation — `selenide click "#submit"`, `selenide setValue "#email" x`,
`selenide code` — is its own short-lived process that connects to the daemon over a loopback socket,
runs one command, prints the result, and exits. Every successful command is recorded (via
`SelenideLogger`, daemon-side) and accumulates, so `selenide code` prints runnable **Selenide Java**.

```
  selenide open <url>   ->  spawns (or reuses) the daemon
  selenide click <sel>  ->  \
  selenide code            >  each invocation talks to the daemon over a loopback socket
  selenide close        ->  /

  [ selenide <cmd> ]  --request-->  [ daemon (JVM): browser + recorder ]  --response-->  [ prints ]
```

## Install (npm)

Published on npm: [selenide-cli](https://www.npmjs.com/package/selenide-cli).

```bash
npm install -g selenide-cli
selenide --version          # -> selenide-cli 7.17.0
```

Requirements: **JDK 17+** on your `PATH` (`java -version`) and a browser (Chrome/Firefox/Edge) —
Selenium Manager downloads the matching driver on first run. The npm package is a thin launcher that
runs the bundled Selenide fat JAR via `java`.

Troubleshooting:
- `Java 17+ is required` / `ENOENT` → install a JDK 17+, or point `JAVA_HOME` at one.
- `command not found: selenide` → add npm's global bin dir to `PATH`:
  `export PATH="$(npm prefix -g)/bin:$PATH"`.

## Build & requirements

- JDK 17+; a browser (Chrome/Firefox/Edge). Selenium Manager downloads the driver automatically.

```bash
# from the repo root
./gradlew :modules:cli:shadowJar
# put a launcher on your PATH (optional but convenient)
JAR=$(ls "$(pwd)"/modules/cli/build/libs/selenide-cli-*.jar)
printf '#!/usr/bin/env bash\nexec java -jar "%s" "$@"\n' "$JAR" | sudo tee /usr/local/bin/selenide >/dev/null
sudo chmod +x /usr/local/bin/selenide
```

Without a launcher, replace `selenide` with `java -jar modules/cli/build/libs/selenide-cli-*.jar`
(the shaded jar is versioned, e.g. `selenide-cli-7.17.0.jar`).

## Quick start

```bash
selenide open --headless https://the-internet.herokuapp.com/login
selenide setValue "#username" tomsmith
selenide setValue "#password" "SuperSecretPassword!"
selenide click "button[type=submit]"
selenide should "text=You logged into a secure area!" visible
selenide code          # print the generated Selenide Java
selenide close         # stop the browser + daemon
```

## Commands

Lifecycle:

| Command | Effect |
|---|---|
| `open [options] <url>` | start/reuse the session's daemon + browser, navigate to `<url>` |
| `close` | close the browser and stop the daemon |
| `list` | list sessions (`running` / `stale`) |
| `close-all` | close every session |

Recorded actions (each appends to the generated code):

```
click <sel>                    setValue <sel> <text>   (alias: type)
append <sel> <text>            clear <sel>
selectOption <sel> <text>      selectRadio <sel> <value>
check <sel> | uncheck <sel>    setSelected <sel> <bool>
hover <sel>                    doubleClick <sel>        contextClick <sel>
scrollTo <sel>                pressEnter|pressTab|pressEscape <sel>
should <sel> <cond> [value]   back | forward | refresh
screenshot [name]
```

Codegen (not recorded): `code`, `save <file>`, `undo`, `reset`.

Full grammar and generated-Java mapping: [../../.github/skills/selenide-cli/references/commands.md](skills/selenide-cli/references/commands.md).

## Selectors & conditions

Selectors are CLI arguments — quote any that contain spaces so the shell passes one token. CSS by
default; `text=<text>` → `byText`, `xpath=<expr>` (or a leading `//`) → `byXpath`.

```bash
selenide click "#submit"
selenide click "button[type=submit]"
selenide click "text=Sign In"
selenide click "xpath=//button[@id='go']"
```

`should` conditions: `visible`, `hidden`, `exist`, `disappear`, `enabled`, `disabled`, `selected`,
`checked`, `editable`, `readonly`, `empty`, `focused`, `text`, `exactText`, `value`, `exactValue`,
`cssClass`, `matchText`, `attribute <name> [value]`.

## Sessions

Run isolated browsers concurrently with `-s <name>` (default session is `default`):

```bash
selenide -s auth   open https://app.example.com/login
selenide -s public open https://example.com
selenide -s auth   click "#login"
selenide list
selenide close-all
```

Daemon state lives under `~/.selenide-cli/` (`<session>.port`, `<session>.log`).

## `open` options

`--browser=<name>` `--headless` `--browser-size=<WxH>` `--base-url=<url>` `--timeout=<ms>`
`--remote=<url>` `--reports-folder=<dir>` … (applied when the daemon is first spawned). Also
`--version`/`-v`, `--help`/`-h`. See
[configuration reference](skills/selenide-cli/references/configuration.md).

## Generated code example

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

## Tests

- Unit tests (`com/codeborne/selenide/cli/**`, incl. Protocol + client↔daemon round-trip) run in
  `./gradlew :modules:cli:check` — no browser.
- The end-to-end test (`integration/**`) needs a browser: `./gradlew :modules:cli:chrome_headless`.
