# Browser configuration

Flags are passed to `open` and map to a Selenide `SelenideConfig`. They take effect **when the
daemon is first spawned** for a session; a later `open` on a live session just navigates (to change
options, `close` the session first). The URL is the one positional argument.

```bash
selenide open [flags] <url>
```

## Flags

| Flag | Effect |
|---|---|
| `--browser=<name>` | `chrome` (default), `firefox`, `edge`, `safari` |
| `--headless` | run the browser headless |
| `--browser-size=<WxH>` | window size, e.g. `1920x1080` |
| `--browser-version=<v>` | request a specific browser version |
| `--browser-binary=<path>` | use a browser binary at a custom path |
| `--browser-position=<XxY>` | window position |
| `--base-url=<url>` | base URL; a relative `open` target resolves against it |
| `--timeout=<ms>` | element/assertion timeout (default 4000) |
| `--polling-interval=<ms>` | polling interval for waits |
| `--remote=<url>` | Selenium Grid / remote WebDriver URL |
| `--page-load-strategy=<s>` | `normal` / `eager` / `none` |
| `--page-load-timeout=<ms>` | page load timeout |
| `--reports-folder=<dir>` | where `screenshot` writes PNGs |
| `--downloads-folder=<dir>` | download target folder |

Top-level: `--version` / `-v`, `--help` / `-h`. Session: `-s <name>` / `--session <name>`.

## Examples

```bash
selenide open --headless --browser=chrome --browser-size=1920x1080 https://selenide.org
selenide open --browser=firefox --base-url=https://example.com /login
selenide open --remote=http://localhost:4444/wd/hub --browser=chrome https://example.com
selenide open --browser-binary=/opt/chrome/chrome --timeout=10000 https://example.com
```

## Notes

- No manual driver setup: **Selenium Manager** downloads the matching driver on first run (the
  daemon must have network access for that initial download, or a pre-provisioned driver).
- Screenshots (`screenshot` command) are written on the **daemon host** under `--reports-folder`
  (default `build/reports/tests`).
