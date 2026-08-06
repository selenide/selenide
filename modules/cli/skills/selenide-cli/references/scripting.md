# Scripting & CI

Because every command is a separate process against a persistent daemon, a flow is just a sequence
of `selenide` invocations in a script.

## A scripted flow

```bash
#!/usr/bin/env bash
set -euo pipefail
CLI="java -jar $(ls modules/cli/build/libs/selenide-cli-*.jar)"   # or the 'selenide' launcher

$CLI open --headless https://the-internet.herokuapp.com/login
$CLI setValue "#username" tomsmith
$CLI setValue "#password" "SuperSecretPassword!"
$CLI click "button[type=submit]"
$CLI should "text=You logged into a secure area!" visible
$CLI save build/login-flow.java     # daemon writes the generated code
$CLI close
```

Each action exits non-zero on failure, so `set -e` aborts the script at the first broken step.

## Capture the generated code

```bash
# ($CLI as defined above)
$CLI code > build/GeneratedFlow.java     # stdout is just the snippet
# or let the daemon write it:
$CLI save build/GeneratedFlow.java
```

## Parallel / isolated sessions

Use `-s <name>` to run independent browsers concurrently (separate cookies, storage, recording):

```bash
selenide -s a open "https://app.com?variant=a" &
selenide -s b open "https://app.com?variant=b" &
wait
selenide -s a screenshot
selenide -s b screenshot
selenide close-all
```

## Notes for CI

- Always pass `--headless` on `open`.
- The daemon needs a browser installed and (on first run) network access for the Selenium Manager
  driver download, or a pre-provisioned driver.
- Client stdout = command result / generated code; errors go to stderr with a non-zero exit code —
  gate your CI on exit codes.
- Always `close` (or `close-all`) at the end of a job so no browser/daemon is left running; clean up
  `~/.selenide-cli/` between jobs if your runner is reused.
- Daemon logs are at `~/.selenide-cli/<session>.log` — surface them on failure.
