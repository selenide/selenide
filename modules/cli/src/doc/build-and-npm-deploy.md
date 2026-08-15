# Selenide CLI — build locally & deploy as an npm package

The Selenide CLI is a Java tool: it builds to a single self-contained ("fat") JAR
(`selenide-cli-<version>.jar`) that acts as both the client and the background daemon. There is no
Node.js code in the CLI itself — the npm package is a thin **launcher** that ships (or downloads)
that JAR and spawns `java -jar …`. This keeps `npm install -g @selenide/cli` giving users a `selenide`
command without them cloning the repo or dealing with Gradle.

- [1. Build the CLI locally](#1-build-the-cli-locally)
- [2. Package for npm](#2-package-for-npm)
- [3. Publish to npm](#3-publish-to-npm)
- [4. Install & use (end users)](#4-install--use-end-users)

---

## 1. Build the CLI locally

### Requirements

- **JDK 17+** on `PATH` — verify with `java -version`.
- A browser (Chrome/Chromium, Firefox, or Edge). Selenium Manager downloads the matching driver on
  first run.

### Build the fat JAR

```bash
# from the Selenide repo root
./gradlew :modules:cli:shadowJar
# -> modules/cli/build/libs/selenide-cli-<version>.jar   (runnable, all deps bundled)

java -jar modules/cli/build/libs/selenide-cli-*.jar --version
```

The JAR is versioned to match the Selenide release (e.g. `selenide-cli-7.18.0.jar`).

### Run it locally

```bash
java -jar modules/cli/build/libs/selenide-cli-*.jar open --headless https://selenide.org
java -jar modules/cli/build/libs/selenide-cli-*.jar click "#submit"
java -jar modules/cli/build/libs/selenide-cli-*.jar code
java -jar modules/cli/build/libs/selenide-cli-*.jar close
```

Optionally drop a `selenide` launcher on your `PATH` so invocations stay short (and the daemon
re-launches itself with the same classpath):

```bash
JAR=$(ls "$(pwd)"/modules/cli/build/libs/selenide-cli-*.jar)
printf '#!/usr/bin/env bash\nexec java -jar "%s" "$@"\n' "$JAR" | sudo tee /usr/local/bin/selenide >/dev/null
sudo chmod +x /usr/local/bin/selenide
selenide --version
```

### Run from Gradle without building the JAR (dev loop)

The daemon subprocess inherits the launching JVM's classpath, so this works straight from sources:

```bash
./gradlew :modules:cli:runCli --console=plain -PcliArgs="open --headless https://selenide.org"
./gradlew :modules:cli:runCli --console=plain -PcliArgs="click #submit"
./gradlew :modules:cli:runCli --console=plain -PcliArgs="code"
```

### Verify the build before shipping

```bash
./gradlew :modules:cli:check            # unit tests (incl. client<->daemon round-trip) + Checkstyle + SpotBugs
./gradlew :modules:cli:chrome_headless  # end-to-end test (needs a browser)
```

---

## 2. Package for npm

The npm package wraps the fat JAR from step 1 in a small Node launcher. Two shipping strategies:

| Strategy | JAR lives… | Pros | Cons |
|---|---|---|---|
| **Bundle the JAR** (recommended to start) | inside the npm tarball | offline install, one artifact, deterministic | larger tarball (~tens of MB) |
| **Download on install** | fetched from a release URL in `postinstall` | tiny tarball | needs network at install time; must host the JAR |

Everything below uses the **bundle** strategy.

### Layout

Create an `npm/` folder next to the module (kept out of the Gradle build):

```
modules/cli/npm/
  package.json
  bin/selenide.js         # Node launcher -> spawns `java -jar <jar>`
  jar/                    # the fat JAR is copied here at pack time (git-ignored)
  README.md               # copy of ../README.md, shown on npmjs.com
  .npmignore
```

### `package.json`

```json
{
  "name": "@selenide/cli",
  "version": "7.18.0",
  "description": "Command-line browser automation & Selenide Java codegen over a background daemon.",
  "bin": { "selenide": "bin/selenide.js" },
  "files": ["bin/", "jar/", "README.md"],
  "engines": { "node": ">=16" },
  "keywords": ["selenide", "selenium", "webdriver", "browser", "automation", "codegen", "cli"],
  "license": "MIT",
  "repository": { "type": "git", "url": "https://github.com/selenide/selenide.git" },
  "homepage": "https://selenide.org",
  "publishConfig": { "access": "public" }
}
```

Keep `version` in lockstep with the Gradle `version` in `build.gradle` (set to `7.18.0` right before
that release is cut), so
`@selenide/cli@X` on npm always ships `selenide-cli-X.jar`. `publishConfig.access: "public"` is
required for a scoped package (`@selenide/...`) to publish publicly instead of defaulting to
private, which requires a paid npm org plan.

### `bin/selenide.js` — the launcher

Requires a JDK 17+ on `PATH` (or `JAVA_HOME`). It forwards all args to the JAR and mirrors its exit
code.

```js
#!/usr/bin/env node
'use strict';

const { spawnSync } = require('node:child_process');
const fs = require('node:fs');
const path = require('node:path');

// The single fat JAR bundled in this package.
const jarDir = path.join(__dirname, '..', 'jar');
const jar = fs.readdirSync(jarDir).find((f) => f.endsWith('.jar'));
if (!jar) {
  console.error('selenide-cli: bundled JAR not found in ' + jarDir);
  process.exit(1);
}

const javaBin = process.env.JAVA_HOME
  ? path.join(process.env.JAVA_HOME, 'bin', 'java')
  : 'java';

const result = spawnSync(javaBin, ['-jar', path.join(jarDir, jar), ...process.argv.slice(2)], {
  stdio: 'inherit',
});

if (result.error) {
  if (result.error.code === 'ENOENT') {
    console.error('selenide-cli: Java 17+ is required. Install a JDK or set JAVA_HOME.');
  } else {
    console.error('selenide-cli: failed to launch java — ' + result.error.message);
  }
  process.exit(1);
}
process.exit(result.status === null ? 1 : result.status);
```

### `.npmignore`

```
# nothing extra to ignore — `files` in package.json is the allow-list.
```

### Assemble the tarball

Build the JAR, copy it into `npm/jar/`, then let `files` in `package.json` decide what ships:

```bash
# from the repo root
./gradlew :modules:cli:shadowJar

cd modules/cli/npm
mkdir -p jar && rm -f jar/*.jar
cp ../build/libs/selenide-cli-*.jar jar/
cp ../README.md README.md

npm pack --dry-run     # inspect exactly what would be published
```

`npm pack --dry-run` should list `bin/selenide.js`, `jar/selenide-cli-<version>.jar`, `README.md`,
and `package.json` — nothing else.

### Smoke-test the package before publishing

```bash
cd modules/cli/npm
npm pack                                  # -> selenide-cli-<version>.tgz (npm strips "@"/"/" from scoped names)
npm install -g ./selenide-cli-*.tgz
selenide --version                        # should print: selenide-cli <version>
selenide open --headless https://selenide.org && selenide close
npm uninstall -g @selenide/cli
```

---

## 3. Publish to npm

Prerequisites: membership in the `@selenide` npm org (or publish rights granted on the package),
and `npm login` done.

```bash
cd modules/cli/npm

# 1. make sure version matches the Gradle build version
node -e "console.log(require('./package.json').version)"   # e.g. 7.18.0

# 2. rebuild + refresh the bundled JAR (see step 2)
(cd ../../.. && ./gradlew :modules:cli:shadowJar)
mkdir -p jar && rm -f jar/*.jar && cp ../build/libs/selenide-cli-*.jar jar/
cp ../README.md README.md

# 3. publish (scoped packages default to private, so --access public is required)
npm publish --access public
```

For a pre-release, tag it so it doesn't become `latest`:

```bash
npm publish --tag next --access public
```

### 2FA is required to publish

npm requires two-factor auth (or a granular token with "bypass 2FA") to publish. Set up **once**
under npmjs.com → *Account* → *Two-Factor Authentication*. Two supported methods:

- **Passkey** (e.g. Google Password Manager / iCloud Keychain) — `npm publish` prints an
  `Authenticate your account at: <url>` line; open it, approve with the passkey, and the terminal
  finishes the publish. No numeric code.
- **Authenticator app (TOTP)** — `npm publish` prompts for a 6-digit code; or pass it inline with
  `npm publish --access public --otp=123456`.

To publish non-interactively (CI), create a **granular access token** with read+write on the package
and "bypass 2FA", then `npm config set //registry.npmjs.org/:_authToken=<token>`.

Notes:
- npm publishes are effectively permanent — a given `name@version` can't be re-published after
  unpublish. Bump the version for every release rather than overwriting.
- Publishing is an outward-facing action; only run `npm publish` when the maintainer has explicitly
  asked to release.

### The old unscoped `selenide-cli` package

The CLI was originally published for testing purposes as `selenide-cli` (unscoped) under a personal
npm account. It's now deprecated in favor of the official `@selenide/cli`, the same move already
made for the MCP server (`selenide-mcp` → `@selenide/mcp`). The deprecated package's source is
`selenide-cli-legacy/` at the repo root — a thin shim whose `index.js` prints a deprecation notice
to stderr and then `require()`s `@selenide/cli/bin/selenide.js` directly (its only dependency is
`@selenide/cli`, so a plain `npm publish` from that directory picks up whatever version range is
declared there). Publish/update it the same way as any other npm package once `@selenide/cli` itself
is live:

```bash
cd selenide-cli-legacy
npm publish   # unscoped package, no --access flag needed
```

---

## 4. Install & use (end users)

```bash
npm install -g @selenide/cli      # requires JDK 17+ on PATH
selenide --version
```

Then the usual flow:

```bash
selenide open --headless https://the-internet.herokuapp.com/login
selenide setValue "#username" tomsmith
selenide setValue "#password" "SuperSecretPassword!"
selenide click "button[type=submit]"
selenide should "text=You logged into a secure area!" visible
selenide code       # print the generated Selenide Java
selenide close      # stop the browser + daemon
```

### Troubleshooting

- **`Java 17+ is required` / `ENOENT`** → install a JDK 17+ and ensure `java` is on `PATH`, or set
  `JAVA_HOME`.
- **`command not found: selenide`** → the npm global `bin` dir isn't on `PATH`; add
  `$(npm prefix -g)/bin` (Unix) to `PATH`.
- **Daemon / browser issues** → see the daemon-lifecycle troubleshooting in
  [../../skills/selenide-cli/references/build-and-run.md](../../skills/selenide-cli/references/build-and-run.md).
