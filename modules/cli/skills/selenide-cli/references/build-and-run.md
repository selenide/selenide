# Build, install, sessions & daemon lifecycle

`selenide-cli` is the `:modules:cli` module of the Selenide repo. It builds to a self-contained
("fat") JAR that acts as both the client and the daemon.

## Requirements

- JDK 17+ on `PATH` (`java -version`).
- A browser (Chrome/Chromium, Firefox, or Edge). Selenium Manager downloads the driver on first run.

## Build

```bash
# from the Selenide repo root
./gradlew :modules:cli:shadowJar
# -> modules/cli/build/libs/selenide-cli-<version>.jar   (runnable, all deps bundled)
java -jar modules/cli/build/libs/selenide-cli-*.jar --version
```

## Install a `selenide` launcher (recommended)

A launcher keeps invocations short (`selenide click ...`) and — importantly — makes the daemon
re-launch itself with the same classpath:

```bash
JAR=$(ls "$(pwd)"/modules/cli/build/libs/selenide-cli-*.jar)
printf '#!/usr/bin/env bash\nexec java -jar "%s" "$@"\n' "$JAR" | sudo tee /usr/local/bin/selenide >/dev/null
sudo chmod +x /usr/local/bin/selenide
selenide open https://selenide.org
```

Without a launcher, prefix every command with `java -jar modules/cli/build/libs/selenide-cli-*.jar`.

## How the daemon is started

`open` spawns the daemon as a **detached JVM** using the current process's classpath
(`java -cp <java.class.path> com.codeborne.selenide.cli.SelenideCli __daemon --session=<s> …`). This
works both from the fat JAR and from a Gradle/dev classpath. The daemon binds a loopback port and
records it in `~/.selenide-cli/<session>.port`; its stdout/stderr go to `~/.selenide-cli/<session>.log`.

## Session lifecycle

```bash
selenide open https://example.com     # start default session
selenide -s work open https://work.example.com

selenide list                          # e.g.  default  running / work  running
selenide close                         # stop default session
selenide -s work close                 # stop the 'work' session
selenide close-all                     # stop everything
```

## Run from Gradle without building the JAR (dev)

The daemon subprocess inherits the launching JVM's classpath, so this works too:

```bash
./gradlew :modules:cli:runCli --console=plain -PcliArgs="open --headless https://selenide.org"
./gradlew :modules:cli:runCli --console=plain -PcliArgs="click #submit"
```

## Verify the build

```bash
./gradlew :modules:cli:check            # unit tests (incl. client↔daemon round-trip) + Checkstyle + SpotBugs
./gradlew :modules:cli:chrome_headless  # end-to-end test (needs a browser)
```

## Troubleshooting

- **`No open session '<name>'`** → run `selenide open <url>` first (for that `-s` name).
- **Stale session** (daemon died) → `selenide list` shows `stale`; `selenide -s <name> open <url>`
  cleans it up and starts fresh, or delete `~/.selenide-cli/<name>.port`.
- **Daemon won't start** → check `~/.selenide-cli/<session>.log` (browser/driver errors appear there).
- **Browser/driver errors** → ensure a browser is installed; `--browser-binary=<path>` for a custom
  location; Selenium Manager resolves the driver.
- **Zombie browsers** → `selenide close-all`; if needed, kill leftover `java`/browser processes and
  remove `~/.selenide-cli/*.port`.
