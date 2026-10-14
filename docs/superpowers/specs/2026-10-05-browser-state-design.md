# Save and restore browser state (#3378)

## Goal

Let tests skip the login flow by saving the browser state (cookies, localStorage, sessionStorage) once and restoring it
in later tests — the Selenide equivalent of Playwright's `storageState`.

Success criteria:

- A user can write a single line in `@BeforeEach` that logs in only when needed and otherwise reuses the saved state.
- A stale saved state (expired cookie, server restart, revoked session) is detected and replaced automatically.
- Low-level methods exist for non-standard flows.

## Scope

- State covers **one origin**: the origin of the page that was open when the state was saved.
- Saved: all cookies visible to WebDriver for the current page (including `httpOnly`), all localStorage items, all
  sessionStorage items, and the origin.
- Implemented with classic WebDriver only (`manage().getCookies()/addCookie()` + JavaScript for storages).
  CDP/BiDi (all-domain cookies, no navigation) may be added later.
- Out of scope: multiple origins, IndexedDB, Service Workers, CDP/BiDi-based cookie access.

## Public API

### High-level: `OpenOptions`

```java
// the typical case: log in only when needed
open("/dashboard", withBrowserState("build/auth.json")
  .loggedInIf(() -> $("#logout").is(visible, Duration.ofSeconds(2)))
  .orLogin(() -> login("bob", "secret")));

// plain restore: the file must exist
open("/dashboard", withBrowserState("build/auth.json"));
open("/dashboard", withBrowserState(BrowserState.fromJson(System.getenv("AUTH_STATE"))));
```

- `Selenide.open(String relativeOrAbsoluteUrl, OpenOptions options)`
- `SelenideDriver.open(String relativeOrAbsoluteUrl, OpenOptions options)`

`OpenOptions` (new, `com.codeborne.selenide`) follows the Selenide `*Options` naming pattern (`ClickOptions`,
`DownloadOptions`, ...): options are named after the action (`open`), not after the object.

- `static OpenOptions withBrowserState(String file)`, `static OpenOptions withBrowserState(Path file)` — lazy: only
  remembers the path; the file is read when the page is being opened. Necessary for `orLogin`, where the file may not
  exist yet.
- `static OpenOptions withBrowserState(BrowserState state)` — in-memory state, no file.
- `OpenOptions loggedInIf(BooleanSupplier check)` — returns a new instance (immutable).
- `OpenOptions orLogin(Runnable login)` — returns a new instance. Throws `IllegalStateException` if `loggedInIf`
  was not called before (restoring without verification is exactly what we want to avoid), or if the options don't
  have a file (there is nowhere to save the fresh state).

### `BrowserState` (new, `com.codeborne.selenide`)

The data: a public immutable record `BrowserState(origin, cookies, localStorage, sessionStorage)`.

- `static BrowserState fromFile(String path)`, `static BrowserState fromFile(Path path)` — reads the file immediately.
- `static BrowserState fromJson(String json)`
- `String toJson()`
- `toString()` contains only cookie names and storage keys, not values (they are credentials).

### Low-level: `BrowserSession`

`WebDriverConditional` (package-private record) is renamed to a public class `BrowserSession`, which still implements
`Conditional<WebDriver>`, so `webdriver().shouldHave(url(...))` keeps working.

- `Selenide.webdriver()` and `SelenideDriver.webdriver()` return `BrowserSession` instead of `Conditional<WebDriver>`.
  Binary-incompatible change, accepted for 7.19.0.
- `BrowserState getBrowserState()` — reads the current state of the current page.
- `void saveBrowserState(Path path)` — `getBrowserState()` + write file.
- `void restoreBrowserState(BrowserState state)` — applies the state to the **current** page. Does not navigate.

```java
webdriver().restoreBrowserState(BrowserState.fromFile("build/auth.json"));
webdriver().saveBrowserState(Path.of("build/auth.json"));
```

## Behavior

### `restoreBrowserState(state)` (low-level)

1. Compare the current page origin (`location.origin`) with `state.origin`. If they differ, throw
   `IllegalStateException`: "Cannot restore browser state of https://a.com while current page is https://b.com.
   Open a page of https://a.com first."
2. Add every cookie (`manage().addCookie`). Same-name cookies are overwritten.
3. `setItem` every localStorage and sessionStorage item. Existing other items are kept (merge, no clear).

### `open(url, withBrowserState(...))` without `orLogin`

1. Resolve `url` the same way `open(url)` does (relative against `baseUrl`). If the resolved URL has a different origin
   than `state.origin`, throw `IllegalArgumentException`.
2. Open `<origin>/favicon.ico` — a cheap page of the target origin that does not run app JS or redirect to login.
   Origins are compared in `location.origin` format: the protocol's default port is omitted.
3. Verify the browser actually ended up on `state.origin` (e.g. no redirect to a CDN). Otherwise fail with a clear
   message.
4. `restoreBrowserState(state)`.
5. `open(url)`.

If the file does not exist, throw an exception naming the file.

### `open(url, withBrowserState(file).loggedInIf(check).orLogin(login))`

1. If the file exists and is valid JSON: steps 1–5 above, then run `check`. If `true` — done.
   If the landing page redirected to another origin, treat the state as not reusable (go to step 2).
2. Otherwise (file missing or invalid, or check returned `false`): if a stale state was restored, clear cookies,
   localStorage and sessionStorage **on the state's own origin** (open its landing page first: the app may have
   redirected to an identity provider, whose cookies must not be deleted); then run `login`, `saveBrowserState(file)`,
   `open(url)`, run `check`.
3. If `check` is still `false` after a fresh login, throw an error saying the check failed right after login
   (so either the login lambda or the check is wrong). No retries, no loops.

### Concurrency and file writes

- The "check file → login → save" sequence is guarded by a per-path lock inside the JVM (a static
  `ConcurrentHashMap<Path, Lock>`), so in parallel tests one thread logs in and the others reuse its file.
  The lock is held only for the decision/login/save part, not for opening the target page.
- Files are written atomically: write to a temp file in the same directory, then `Files.move(..., ATOMIC_MOVE)`.
  Parent directories are created if missing.

### JSON format

Serialized via Selenium's `org.openqa.selenium.json.Json` — no new dependency.

```json
{
  "origin": "https://app.example.com",
  "cookies": [
    {"name": "SID", "value": "...", "domain": "app.example.com", "path": "/", "expiry": 1791234567,
     "secure": true, "httpOnly": true, "sameSite": "Lax"}
  ],
  "localStorage": {"token": "..."},
  "sessionStorage": {}
}
```

`expiry` is epoch seconds, omitted for session cookies. `sameSite` omitted when absent.

### Logging

`open(url, options)`, `saveBrowserState` and `restoreBrowserState` are each reported as one `SelenideLogger` step.
Cookie values and storage values are **not** logged (they are credentials).

## Testing

Unit tests (`src/test/java/com/codeborne/selenide/`):

- JSON round trip: all cookie fields, session cookie (no expiry), empty maps, unicode values.
- `orLogin` without `loggedInIf` throws; `orLogin` on an in-memory state throws.
- `withBrowserState(file)` of a missing file throws a clear error on use (not at construction);
  `BrowserState.fromFile` throws immediately.
- Atomic save creates parent directories.

Integration tests (`src/test/java/integration/`):

- Set cookies (including an httpOnly cookie set by the test server), localStorage and sessionStorage; save; close the
  browser; open a new one; `open(url, withBrowserState(file))`; verify everything is restored.
- `open(url, withBrowserState(state))` with an in-memory state.
- `loggedInIf/orLogin`: file missing → login runs once and file is created; file valid → login not called; check fails
  on restored state → login runs again and file is overwritten; invalid JSON → login; app redirects to another origin
  → stale state is cleared on its own origin; check fails after login → clear error.
- `restoreBrowserState` on a different origin → clear error.
- `open(url, withBrowserState(..))` with an absolute URL on another origin → clear error.
- `webdriver().shouldHave(url(...))` still works (return type change).
