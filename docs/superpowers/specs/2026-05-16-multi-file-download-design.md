# Multi-File Download — Design

**Issue:** [#3261 — Selenide allows downloading multiple files with a single click](https://github.com/selenide/selenide/issues/3261)
**Date:** 2026-05-16
**Branch:** `feat/3261-multi-file-download`

## Problem

When a single click triggers multiple file downloads, Selenide's `$.download(...)`
returns only the first file. The remaining files are silently discarded. The
method signature returns `File`, so it cannot express "many files."

The browser is downloading all the files — Selenide's `DownloadFileToFolder` already
snapshots the downloads folder, watches for new files matching the filter, and waits
for `.crdownload`/`.part`/`.tmp` to disappear. It just stops at the first match.

## Goal

Let users download N files from a single user action and assert on them. The API
must be deterministic and fail loudly when reality doesn't match the declared
expectation — no quiet-period heuristics that flake under load.

## Non-goals

- Streaming / incremental "yield each file as it arrives" APIs.
- Range expectations ("between 2 and 5 files"). Add later if requested.
- Retroactive collection of files downloaded before the call.

## Public API

One new method on `SelenideElement`:

```java
List<File> downloadFiles(DownloadFilesOptions options) throws FileNotDownloadedError;
```

New class `com.codeborne.selenide.DownloadFilesOptions` — a sibling of
`DownloadOptions`, **not** a subclass and not a builder on it. The expected
file count is a constructor-level requirement, expressed via a factory:

```java
public static DownloadFilesOptions files(int expectedFileCount);
```

The class exposes the same builder shape as `DownloadOptions` for everything
the two share — `withMethod`, `withTimeout`, `withIncrementTimeout`,
`withFilter`, `withExtension`, `withName`, `withNameMatching`, `withoutContent`,
`withAction`. It does **not** expose anything that would let the count drift
(no `withExpectedFileCount` builder — the count is fixed by `files(N)`).

`DownloadOptions` is **unchanged**. The two classes share no inheritance; both
implement `HasTimeout` (which `DownloadOptions` already does).

Usage:

```java
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.DownloadFilesOptions.files;

File pdf = $("#export").download(
    file().withExtension("pdf").withTimeout(ofSeconds(30))
);

List<File> pdfs = $("#exportAll").downloadFiles(
    files(3).withExtension("pdf").withTimeout(ofSeconds(30))
);
```

### Calling rules

- `files(int n)` validates `n >= 1` at construction; otherwise throws
  `IllegalArgumentException("expectedFileCount must be >= 1, got: <n>")`.
- The misuse cases that required runtime checks under the old design — "count
  not set," "count > 1 passed to single-file `download()`" — are now ruled out
  by the type system. `download(DownloadOptions)` cannot accept multi-file
  options; `downloadFiles(DownloadFilesOptions)` cannot be called with N < 1.
- The returned `List<File>` is sorted **in completion order, oldest first**.
  Implementation: for FOLDER/CDP modes this is file modification time; for
  PROXY mode this is the HTTP response capture timestamp. Ties broken by
  filename, ascending.
- Files are archived (moved to per-test folder) using the existing
  `DownloadFileToFolder.archiveFile(...)` logic — same layout, one file per archived path.

## Stop condition & timing (FOLDER / CDP modes)

Both folder-based modes share `DownloadFileToFolder`. The download loop becomes:

1. Snapshot `previousFiles`.
2. `action.perform(driver, clickable)` (default: click).
3. Poll until **all** hold simultaneously:
   - count of new files matching `filter` == `expectedFileCount`, AND
   - no temp files (`.crdownload`/`.part`/`.tmp` per browser) matching `filter` remain.
4. Fail-fast paths:
   - **Too many files**: at any poll, if matching new files > `expectedFileCount`,
     throw `FileNotDownloadedError("Expected N files, but found M new files matching <filter>: [...]")`.
   - **Stalled**: existing `failFastIfNoChanges` (per-`incrementTimeout`) keeps
     working — if no folder modifications for `incrementTimeout` ms and we still
     don't have N files, fail.
   - **Hard timeout**: existing `timeout` still bounds the whole wait. On timeout
     with fewer than N matching files, throw
     `FileNotDownloadedError("Failed to download N files in <timeout>: only M files matched <filter>. Files found: [...]")`.
5. On success, archive each file and return sorted by mtime ascending.

`pollingInterval`, `incrementTimeout`, archive layout, and the existing
`ContentStrategy` (FULL_CONTENT / EMPTY_CONTENT) are unchanged. The single-file
`download(...)` path remains identical to today — it just delegates to the same
code with `expectedFileCount = 1`.

### PROXY mode

`DownloadFileWithProxyServer` today captures the first matching HTTP response after
the click. We extend it to keep collecting responses until either `expectedFileCount`
match the filter or `timeout` elapses. If more than `expectedFileCount` match, fail
fast with the same "too many files" error. Files are sorted by response capture
timestamp, oldest first.

### HTTPGET mode

`DownloadFileWithHttpRequest` fetches the element's `href` directly. An element has
exactly one `href`, so multi-file is meaningless.

- `expectedFileCount == 1` → behaves identically to single-file `download()`,
  returns a one-element list.
- `expectedFileCount > 1` → throws
  `UnsupportedOperationException("HTTPGET mode downloads a single href; use FileDownloadMode.FOLDER, CDP, or PROXY for multi-file downloads")`.

## Internal refactor

Minimal, focused. No unrelated cleanup.

### `DownloadFileToFolder`

- Generalize the existing `download(...)` so the "wait for new files / wait for
  temp files / archive" pipeline accepts `expectedFileCount` and returns
  `List<File>`.
- Public `downloadFiles(...)` returning `List<File>`.
- Public `download(...)` returning `File` remains — internally calls the same
  pipeline with `expectedFileCount = 1` and returns `list.get(0)`.
- `waitForNewFiles` and `waitUntilDownloadsCompleted` get an `int expectedCount`
  parameter so they keep polling until count == N instead of returning on the
  first match. The "too many files" fail-fast check lives in the same loops.

### `Downloads`

- Keep `firstDownloadedFile(timeout, filter)` for back-compat.
- Add `matchingFiles(filter)` returning all filtered files sorted by mtime asc
  with filename as tiebreaker.

### `DownloadFilesOptions` (new)

- New class `com.codeborne.selenide.DownloadFilesOptions`, parallel to
  `DownloadOptions`. Private constructor; `public static DownloadFilesOptions files(int)`
  is the only entry point.
- Fields: `int expectedFileCount` (final, validated `>= 1` in factory), plus the
  same fields as `DownloadOptions` (`method`, `timeout`, `incrementTimeout`,
  `filter`, `action`, `contentStrategy`).
- Builders: `withMethod`, `withTimeout(long)`, `withTimeout(Duration)`,
  `withIncrementTimeout`, `withFilter`, `withExtension`, `withName`,
  `withNameMatching`, `withoutContent`, `withAction`. Same shape as
  `DownloadOptions`. **No** `withExpectedFileCount` — the count is fixed at
  construction.
- Accessors: `expectedFileCount()`, plus the same accessors as `DownloadOptions`.
- Implements `HasTimeout`.
- `toString()` includes `expectedFileCount` plus the same fields as
  `DownloadOptions.toString()`.

### `DownloadOptions`

- **Unchanged.** Existing single-file behavior is untouched.

### `SelenideElement`

- Add `List<File> downloadFiles(DownloadFilesOptions options)` with javadoc
  mirroring the existing `download(DownloadOptions)` style. Include a usage
  example and link to `DownloadFilesOptions.files(int)`.

### Commands

- Add `commands/DownloadFiles` as a sibling of the existing `commands/DownloadFile`.
  Dispatches by `FileDownloadMode` to the appropriate implementation
  (`DownloadFileToFolder.downloadFiles`, `DownloadFileWithCdp.downloadFiles`,
  `DownloadFileWithProxyServer.downloadFiles`, `DownloadFileWithHttpRequest`).
- Existing `commands/DownloadFile` is unchanged.

### Mode-specific implementations

- `DownloadFileWithCdp` — uses folder-based detection internally; extend the same
  way as `DownloadFileToFolder`.
- `DownloadFileWithProxyServer` — collect multiple matching responses, sort by
  capture timestamp.
- `DownloadFileWithHttpRequest` — accept `expectedFileCount == 1`, throw
  `UnsupportedOperationException` for `> 1`.

No new packages. New public surface is exactly: one method on `SelenideElement`
(`downloadFiles`) and one new class (`DownloadFilesOptions`) with its static
factory `files(int)`. `DownloadOptions` is untouched.

## Errors

| Situation | Exception | Message |
|---|---|---|
| `files(n)` with `n < 1` | `IllegalArgumentException` | `expectedFileCount must be >= 1, got: <n>` |
| HTTPGET mode with `expectedFileCount > 1` | `UnsupportedOperationException` | `HTTPGET mode downloads a single href; use FileDownloadMode.FOLDER, CDP, or PROXY for multi-file downloads` |
| Timed out, fewer files than expected | `FileNotDownloadedError` | `Failed to download N files in <timeout>: only M files matched <filter>. Files found: [...]` |
| More files than expected | `FileNotDownloadedError` | `Expected N files, but found M new files matching <filter>: [...]` |
| Stalled (incrementTimeout, no folder changes) | `FileNotDownloadedError` | Existing message, unchanged |

The "count not set" and "count > 1 passed to single-file download()" failure
modes are eliminated by the type system: `download` takes `DownloadOptions`,
`downloadFiles` takes `DownloadFilesOptions`, and `expectedFileCount` is a
constructor argument on the latter.

## Testing

Per CLAUDE.md: prefer integration tests for browser-related actions, unit tests
for algorithmic code. Selenide test conventions: integration tests under
`src/test/java/integration/` extend `ITest`; tests run with Turkish locale.

### New integration test: `MultiFileDownloadTest extends ITest`

- New test page `page_with_multi_download.html` containing a button whose click
  triggers downloads of three distinct files (e.g., JS creates and clicks three
  `<a download>` elements in sequence).
- For each of FOLDER, CDP, PROXY modes:
  - Happy path: 3 files returned, sorted by mtime ascending, content matches.
  - `withExtension` / `withNameMatching` filter narrows what counts toward N.
  - Fewer files than expected → `FileNotDownloadedError` after timeout, message
    mentions the count and filter.
  - More files than expected → fail-fast `FileNotDownloadedError`.
- HTTPGET mode with `expectedFileCount > 1` → `UnsupportedOperationException`
  with the expected message.
- Regression: single-file `download(DownloadOptions)` on the same page still
  returns one `File` and ignores the additional downloads (current behavior).

### New unit tests

- `DownloadFilesOptionsTest`:
  - `files(int)` factory: valid `>= 1` constructs an instance with that count.
  - `files(0)` and `files(-1)` throw `IllegalArgumentException`.
  - Each builder method (`withMethod`, `withTimeout`, `withFilter`,
    `withExtension`, `withName`, `withNameMatching`, `withoutContent`,
    `withAction`, `withIncrementTimeout`) returns a new instance with the
    expected field set and `expectedFileCount` preserved.
  - `toString()` includes `expectedFileCount` and the same fields as
    `DownloadOptions.toString()`.
- `DownloadsTest`:
  - `matchingFiles(filter)` returns the full filtered list.
  - Sort order is mtime ascending; filename breaks ties.

### Javadoc

- `SelenideElement.downloadFiles` javadoc mirrors the existing
  `download(DownloadOptions)` style.
- `./gradlew javadocForSite` must remain green (CLAUDE.md gate).

## Out of scope

- `downloadFiles()` no-args / `downloadFiles(int)` convenience overloads. Users
  go through `DownloadOptions`. Add overloads later only if real demand emerges.
- Returning `List<DownloadedFile>` (the metadata-rich variant suggested in the
  issue comments). The asymmetry with single-file `download()` returning `File`
  is harder to justify than the missing metadata. If needed, add a separate
  `downloadedFiles(...)` later that returns `List<DownloadedFile>`.
- Range expectations (`withMinFiles` / `withMaxFiles`). Add later if requested.