# Multi-File Download Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add `SelenideElement.downloadFiles(DownloadFilesOptions)` returning `List<File>` so a single click that triggers multiple browser downloads returns all of them deterministically.

**Architecture:** New sibling class `DownloadFilesOptions` with static factory `files(int expectedFileCount)`. New `DownloadFiles` command dispatches by `FileDownloadMode`. The folder-based code paths (FOLDER and CDP) generalize their "wait for new files" loop to wait until exactly N matching files are present and no temp files remain, then archive all of them into one per-call folder. PROXY collects N matching responses. HTTPGET rejects N > 1.

**Tech Stack:** Java 17, Selenium WebDriver 4.44.0, JUnit Jupiter 6.0.3, Mockito, AssertJ. Build: Gradle 9.5.1. Code style: 2-space indent, max line 136, Checkstyle `maxWarnings = 0`, SpotBugs effort MAX, no star imports.

**Spec:** `docs/superpowers/specs/2026-05-16-multi-file-download-design.md`
**Issue:** [#3261](https://github.com/selenide/selenide/issues/3261)
**Branch:** `feat/3261-multi-file-download` (already created)

---

## File Map

**Create:**
- `src/main/java/com/codeborne/selenide/DownloadFilesOptions.java` — sibling of `DownloadOptions`, holds `expectedFileCount` plus the shared options
- `src/main/java/com/codeborne/selenide/commands/DownloadFiles.java` — dispatches by `FileDownloadMode`
- `src/test/java/com/codeborne/selenide/DownloadFilesOptionsTest.java` — unit tests for the new options class
- `src/test/java/com/codeborne/selenide/commands/DownloadFilesTest.java` — unit tests for command dispatch & HTTPGET rejection
- `statics/src/test/java/integration/MultiFileDownloadToFolderTest.java` — integration test, FOLDER mode
- `statics/src/test/java/integration/MultiFileDownloadWithCdpTest.java` — integration test, CDP mode
- `modules/proxy/src/test/java/integration/proxy/MultiFileDownloadViaProxyTest.java` — integration test, PROXY mode

**Modify:**
- `src/main/java/com/codeborne/selenide/SelenideElement.java` — add `downloadFiles(DownloadFilesOptions)`
- `src/main/java/com/codeborne/selenide/impl/Downloads.java` — add `matchingFiles(FileFilter)` sorted by mtime asc + name
- `src/main/java/com/codeborne/selenide/impl/DownloadFileToFolder.java` — add `downloadFiles(...)`, generalize wait loops to `expectedFileCount`, archive all into one folder
- `src/main/java/com/codeborne/selenide/impl/DownloadFileWithCdp.java` — add `downloadFiles(...)` with same semantics
- `src/main/java/com/codeborne/selenide/impl/DownloadFileWithProxyServer.java` — add `downloadFiles(...)`
- `src/main/java/com/codeborne/selenide/impl/DownloadFileWithHttpRequest.java` — add `downloadFiles(...)` that rejects N > 1
- `src/main/java/com/codeborne/selenide/commands/Commands.java` — register `"downloadFiles"`
- `src/test/java/com/codeborne/selenide/impl/DownloadFileToFolderTest.java` — add unit tests for `downloadFiles`

---

## Conventions

- 2-space indent, max line 136. No star imports. No `junit` / `org.hamcrest` imports.
- All work happens on branch `feat/3261-multi-file-download` (already created and checked out).
- Commit message style: `#3261 short description` (matches CLAUDE.md: "Commit messages should start with issue ID: `#ID description`").
- After every implementation task, run `./gradlew check` to catch checkstyle + SpotBugs + unit test regressions.

---

## Task 1: `DownloadFilesOptions` class

**Files:**
- Create: `src/main/java/com/codeborne/selenide/DownloadFilesOptions.java`
- Create: `src/test/java/com/codeborne/selenide/DownloadFilesOptionsTest.java`

- [ ] **Step 1: Write the failing unit tests**

Create `src/test/java/com/codeborne/selenide/DownloadFilesOptionsTest.java`:

```java
package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class DownloadFilesOptionsTest {
  @Test
  void factoryRequiresPositiveCount() {
    assertThatThrownBy(() -> files(0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("expectedFileCount must be >= 1");
    assertThatThrownBy(() -> files(-1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("expectedFileCount must be >= 1");
  }

  @Test
  void defaultOptions() {
    DownloadFilesOptions options = files(3);

    assertThat(options.expectedFileCount()).isEqualTo(3);
    assertThat(options.getMethod()).isNull();
    assertThat(options.timeout()).isNull();
    assertThat(options.getFilter()).isEqualTo(none());
  }

  @Test
  void withMethodPreservesCount() {
    DownloadFilesOptions options = files(3).withMethod(PROXY);

    assertThat(options.expectedFileCount()).isEqualTo(3);
    assertThat(options.getMethod()).isEqualTo(PROXY);
  }

  @Test
  void withTimeoutMillisPreservesCount() {
    DownloadFilesOptions options = files(2).withTimeout(9999);

    assertThat(options.expectedFileCount()).isEqualTo(2);
    assertThat(options.timeout()).isEqualTo(Duration.ofMillis(9999));
  }

  @Test
  void withTimeoutDurationPreservesCount() {
    DownloadFilesOptions options = files(2).withTimeout(Duration.ofSeconds(5));

    assertThat(options.expectedFileCount()).isEqualTo(2);
    assertThat(options.timeout()).isEqualTo(Duration.ofSeconds(5));
  }

  @Test
  void withFilterPreservesCount() {
    DownloadFilesOptions options = files(2).withExtension("pdf");

    assertThat(options.expectedFileCount()).isEqualTo(2);
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("pdf"));
  }

  @Test
  void chainedSettings() {
    DownloadFilesOptions options = files(4)
      .withMethod(FOLDER)
      .withExtension("ppt")
      .withTimeout(Duration.ofMillis(1234));

    assertThat(options.expectedFileCount()).isEqualTo(4);
    assertThat(options.getMethod()).isEqualTo(FOLDER);
    assertThat(options.timeout()).isEqualTo(Duration.ofMillis(1234));
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("ppt"));
  }

  @Test
  void printsOptionsToTestReport() {
    assertThat(files(3))
      .hasToString("expectedFileCount: 3");

    assertThat(files(3).withMethod(PROXY).withTimeout(9999))
      .hasToString("expectedFileCount: 3, method: PROXY, timeout: 9.999s");

    assertThat(files(2).withExtension("pdf"))
      .hasToString("expectedFileCount: 2, with extension \"pdf\"");
  }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew check --tests DownloadFilesOptionsTest`
Expected: COMPILE FAILURE — class `DownloadFilesOptions` does not exist.

- [ ] **Step 3: Create the class**

Create `src/main/java/com/codeborne/selenide/DownloadFilesOptions.java`:

```java
package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.impl.DurationFormat;
import com.codeborne.selenide.impl.HasTimeout;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.stream.Stream;

import static com.codeborne.selenide.DownloadOptions.ContentStrategy.EMPTY_CONTENT;
import static com.codeborne.selenide.DownloadOptions.ContentStrategy.FULL_CONTENT;
import static com.codeborne.selenide.files.DownloadActions.click;
import static com.codeborne.selenide.files.FileFilters.none;
import static java.util.stream.Collectors.joining;

public class DownloadFilesOptions implements HasTimeout {
  private static final DurationFormat df = new DurationFormat();

  private final int expectedFileCount;
  @Nullable private final FileDownloadMode method;
  @Nullable private final Duration timeout;
  @Nullable private final Duration incrementTimeout;
  private final FileFilter filter;
  private final DownloadAction action;
  private final DownloadOptions.ContentStrategy contentStrategy;

  private DownloadFilesOptions(int expectedFileCount,
                               @Nullable FileDownloadMode method,
                               @Nullable Duration timeout,
                               @Nullable Duration incrementTimeout,
                               FileFilter filter,
                               DownloadAction action,
                               DownloadOptions.ContentStrategy contentStrategy) {
    this.expectedFileCount = expectedFileCount;
    this.method = method;
    this.timeout = timeout;
    this.incrementTimeout = incrementTimeout;
    this.filter = filter;
    this.action = action;
    this.contentStrategy = contentStrategy;
  }

  public static DownloadFilesOptions files(int expectedFileCount) {
    if (expectedFileCount < 1) {
      throw new IllegalArgumentException("expectedFileCount must be >= 1, got: " + expectedFileCount);
    }
    return new DownloadFilesOptions(expectedFileCount, null, null, null, none(), click(), FULL_CONTENT);
  }

  public int expectedFileCount() {
    return expectedFileCount;
  }

  @Nullable
  public FileDownloadMode getMethod() {
    return method;
  }

  @Nullable
  @Override
  public Duration timeout() {
    return timeout;
  }

  @Nullable
  public Duration incrementTimeout() {
    return incrementTimeout;
  }

  public FileFilter getFilter() {
    return filter;
  }

  public DownloadAction getAction() {
    return action;
  }

  public DownloadOptions.ContentStrategy contentStrategy() {
    return contentStrategy;
  }

  public DownloadFilesOptions withMethod(FileDownloadMode method) {
    return new DownloadFilesOptions(expectedFileCount, method, timeout, incrementTimeout, filter, action, contentStrategy);
  }

  public DownloadFilesOptions withTimeout(long timeoutMs) {
    return new DownloadFilesOptions(expectedFileCount, method, Duration.ofMillis(timeoutMs),
      incrementTimeout, filter, action, contentStrategy);
  }

  public DownloadFilesOptions withTimeout(Duration timeout) {
    return new DownloadFilesOptions(expectedFileCount, method, timeout, incrementTimeout, filter, action, contentStrategy);
  }

  public DownloadFilesOptions withIncrementTimeout(Duration incrementTimeout) {
    return new DownloadFilesOptions(expectedFileCount, method, timeout, incrementTimeout, filter, action, contentStrategy);
  }

  public DownloadFilesOptions withFilter(FileFilter filter) {
    return new DownloadFilesOptions(expectedFileCount, method, timeout, incrementTimeout, filter, action, contentStrategy);
  }

  public DownloadFilesOptions withExtension(String extension) {
    return withFilter(FileFilters.withExtension(extension));
  }

  public DownloadFilesOptions withName(String fileName) {
    return withFilter(FileFilters.withName(fileName));
  }

  public DownloadFilesOptions withNameMatching(String fileNameRegex) {
    return withFilter(FileFilters.withNameMatching(fileNameRegex));
  }

  public DownloadFilesOptions withoutContent() {
    return new DownloadFilesOptions(expectedFileCount, method, timeout, incrementTimeout, filter, action, EMPTY_CONTENT);
  }

  public DownloadFilesOptions withAction(DownloadAction action) {
    return new DownloadFilesOptions(expectedFileCount, method, timeout, incrementTimeout, filter, action, contentStrategy);
  }

  @Override
  public String toString() {
    return Stream.of(
        "expectedFileCount: %d".formatted(expectedFileCount),
        method == null ? null : "method: %s".formatted(method.name()),
        timeout == null ? null : "timeout: %s".formatted(df.format(timeout)),
        incrementTimeout == null ? null : "incrementTimeout: %s".formatted(df.format(incrementTimeout)),
        filter.isEmpty() ? null : filter.toString()
      ).filter(p -> p != null)
      .collect(joining(", "));
  }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew check --tests DownloadFilesOptionsTest`
Expected: PASS, 8 tests green. Also check no checkstyle violations.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/codeborne/selenide/DownloadFilesOptions.java \
        src/test/java/com/codeborne/selenide/DownloadFilesOptionsTest.java
git commit -m "$(cat <<'EOF'
#3261 add DownloadFilesOptions

Sibling of DownloadOptions with a required expectedFileCount set via the
files(int) factory. Mirrors the builder shape of DownloadOptions for the
fields they share.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 2: `Downloads.matchingFiles(FileFilter)` sorted

**Files:**
- Modify: `src/main/java/com/codeborne/selenide/impl/Downloads.java`
- Create: `src/test/java/com/codeborne/selenide/impl/DownloadsMatchingFilesTest.java`

- [ ] **Step 1: Write the failing unit tests**

Create `src/test/java/com/codeborne/selenide/impl/DownloadsMatchingFilesTest.java`:

```java
package com.codeborne.selenide.impl;

import com.codeborne.selenide.files.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class DownloadsMatchingFilesTest {

  @Test
  void matchingFilesReturnsAllMatchesSortedByMtimeAsc() {
    DownloadedFile newer = new DownloadedFile(new File("b.pdf"), 200L, 0, emptyMap());
    DownloadedFile older = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    DownloadedFile newest = new DownloadedFile(new File("c.pdf"), 300L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(newer, older, newest));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf", "b.pdf", "c.pdf");
  }

  @Test
  void matchingFilesBreaksTiesByName() {
    DownloadedFile b = new DownloadedFile(new File("b.pdf"), 100L, 0, emptyMap());
    DownloadedFile a = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(b, a));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf", "b.pdf");
  }

  @Test
  void matchingFilesAppliesFilter() {
    DownloadedFile pdf = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    DownloadedFile txt = new DownloadedFile(new File("b.txt"), 100L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(pdf, txt));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf");
  }

  @Test
  void matchingFilesEmpty() {
    Downloads downloads = new Downloads(List.of());

    assertThat(downloads.matchingFiles(none())).isEmpty();
  }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew check --tests DownloadsMatchingFilesTest`
Expected: COMPILE FAILURE — `matchingFiles` method does not exist on `Downloads`.

- [ ] **Step 3: Add `matchingFiles` to `Downloads`**

In `src/main/java/com/codeborne/selenide/impl/Downloads.java`, add the import and method (between the existing `files(FileFilter)` and `firstMatchingFile` methods):

```java
import java.util.Comparator;
```

```java
  public List<DownloadedFile> matchingFiles(FileFilter fileFilter) {
    return files.stream()
      .filter(fileFilter::match)
      .sorted(Comparator
        .comparingLong((DownloadedFile f) -> f.getFile().lastModified())
        .thenComparing(DownloadedFile::getName))
      .collect(toList());
  }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew check --tests DownloadsMatchingFilesTest`
Expected: PASS, 4 tests green.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/codeborne/selenide/impl/Downloads.java \
        src/test/java/com/codeborne/selenide/impl/DownloadsMatchingFilesTest.java
git commit -m "$(cat <<'EOF'
#3261 add Downloads.matchingFiles sorted by mtime asc

Returns all filtered DownloadedFile entries in completion order, with
filename as the tiebreaker. Used by the upcoming multi-file download path.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 3: `DownloadFileToFolder.downloadFiles(...)` (FOLDER mode core)

**Files:**
- Modify: `src/main/java/com/codeborne/selenide/impl/DownloadFileToFolder.java`
- Modify: `src/test/java/com/codeborne/selenide/impl/DownloadFileToFolderTest.java`

This is the heart of FOLDER-mode multi-file. Generalize the wait + collection logic so the existing single-file `download(...)` continues to work and the new `downloadFiles(...)` returns N archived files, all in one per-call folder, sorted by mtime asc.

- [ ] **Step 1: Write the failing unit tests**

Append to `src/test/java/com/codeborne/selenide/impl/DownloadFileToFolderTest.java` (right after the existing `tracksForNewFilesInDownloadsFolder` test). Add imports too — `DownloadFilesOptions`, `List`, `Comparator`.

```java
  @Test
  void downloadFilesReturnsAllNewMatchingFilesSortedByMtime() throws IOException {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("first.txt"), "1", UTF_8);
      writeStringToFile(downloadsFolder.file("second.txt"), "22", UTF_8);
      writeStringToFile(downloadsFolder.file("third.txt"), "333", UTF_8);
      return null;
    }).when(link).click();

    List<File> files = command.downloadFiles(linkWithHref, link, 3000, 300,
      DownloadFilesOptions.files(3).withMethod(FOLDER));

    assertThat(files).hasSize(3);
    assertThat(files).extracting(File::getName)
      .containsExactlyInAnyOrder("first.txt", "second.txt", "third.txt");
    for (File file : files) {
      assertThat(file.getParentFile()).isNotEqualTo(downloadsFolder.getFolder());
    }
    // All three end up in the same per-call archive folder
    assertThat(files.stream().map(File::getParentFile).distinct().toList()).hasSize(1);
  }

  @Test
  void downloadFilesAppliesFilter() throws IOException {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("report.pdf"), "pdf", UTF_8);
      writeStringToFile(downloadsFolder.file("notes.txt"), "txt", UTF_8);
      writeStringToFile(downloadsFolder.file("summary.pdf"), "pdf2", UTF_8);
      return null;
    }).when(link).click();

    List<File> files = command.downloadFiles(linkWithHref, link, 3000, 300,
      DownloadFilesOptions.files(2).withMethod(FOLDER).withExtension("pdf"));

    assertThat(files).extracting(File::getName)
      .containsExactlyInAnyOrder("report.pdf", "summary.pdf");
  }

  @Test
  void downloadFilesFailsFastIfTooManyMatchingFilesAppear() throws IOException {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("a.txt"), "a", UTF_8);
      writeStringToFile(downloadsFolder.file("b.txt"), "b", UTF_8);
      writeStringToFile(downloadsFolder.file("c.txt"), "c", UTF_8);
      return null;
    }).when(link).click();

    assertThatThrownBy(() ->
      command.downloadFiles(linkWithHref, link, 3000, 300,
        DownloadFilesOptions.files(2).withMethod(FOLDER))
    )
      .isInstanceOf(com.codeborne.selenide.ex.FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files")
      .hasMessageContaining("found 3");
  }
```

Add the imports needed by these tests (top of file, alphabetical, no star imports):

```java
import com.codeborne.selenide.DownloadFilesOptions;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew check --tests DownloadFileToFolderTest`
Expected: COMPILE FAILURE — `command.downloadFiles(...)` method does not exist.

- [ ] **Step 3: Generalize the wait/collect pipeline in `DownloadFileToFolder`**

In `src/main/java/com/codeborne/selenide/impl/DownloadFileToFolder.java`, do the following changes:

**3a. Add the public `downloadFiles` entry method** (place it next to the existing public `download(...)` overloads, around line 46–58):

```java
  public List<File> downloadFiles(WebElementSource link,
                                  WebElement clickable, long timeout, long requestedIncrementTimeout,
                                  DownloadFilesOptions options) {
    return collect(link, clickable, timeout, requestedIncrementTimeout,
      options.expectedFileCount(), options.getFilter(), options.getAction(), options.contentStrategy());
  }
```

**3b. Convert the existing private `download(...)` (the one with `ContentStrategy`, line 60) into a private `collect(...)` returning `List<File>` and parameterize the wait loops with `expectedCount`.** The original returns a single `File`; the new one returns `List<File>`. Public `download(...)` (around line 46) delegates to `collect` with `expectedCount=1` and returns `list.get(0)`.

Updated key body:

```java
  public File download(WebElementSource link,
                       WebElement clickable, long timeout, long requestedIncrementTimeout, DownloadOptions options) {
    return collect(link, clickable, timeout, requestedIncrementTimeout,
      1, options.getFilter(), options.getAction(), options.contentStrategy()).get(0);
  }

  @Deprecated
  public File download(WebElementSource link,
                       WebElement clickable, long timeout, long incrementTimeout,
                       FileFilter fileFilter,
                       DownloadAction action) {
    return collect(link, clickable, timeout, incrementTimeout, 1, fileFilter, action, FULL_CONTENT).get(0);
  }

  private List<File> collect(WebElementSource link,
                             WebElement clickable, long timeout, long requestedIncrementTimeout,
                             int expectedCount,
                             FileFilter fileFilter,
                             DownloadAction action,
                             ContentStrategy contentStrategy) {
    long incrementTimeout = Math.max(requestedIncrementTimeout, 1000);
    Driver driver = link.driver();
    WebDriver webDriver = driver.getWebDriver();
    Config config = driver.config();
    long pollingInterval = Math.max(config.pollingInterval(), 50);
    DownloadsFolder folder = getDownloadsFolder(driver);
    long start = currentTimeMillis();

    if (folder == null) {
      throw new IllegalStateException("Downloads folder is not configured");
    }

    folder.cleanupBeforeDownload();
    List<DownloadedFile> previousFiles = folder.files();

    action.perform(driver, clickable);

    waitForExpectedFiles(driver, fileFilter, folder, previousFiles, expectedCount,
      timeout, incrementTimeout, pollingInterval);
    waitUntilDownloadsCompleted(driver, folder, fileFilter, timeout, incrementTimeout, pollingInterval);

    Downloads newDownloads = new Downloads(folder.filesExcept(previousFiles));
    if (log.isInfoEnabled()) {
      log.info("Downloaded files in {}: {}", folder, newDownloads.filesAsString());
    }
    if (log.isDebugEnabled()) {
      log.debug("All downloaded files: {}", folder.filesAsString());
    }

    List<DownloadedFile> matching = newDownloads.matchingFiles(fileFilter);
    if (matching.size() < expectedCount) {
      String message = String.format(
        "Failed to download %d files in %s: only %d files matched %s. Files found: %s",
        expectedCount, df.format(timeout), matching.size(), fileFilter.description(),
        newDownloads.filesAsString());
      throw new FileNotDownloadedError(message, timeout);
    }
    if (matching.size() > expectedCount) {
      String message = String.format(
        "Expected %d files, but found %d new files matching %s: %s",
        expectedCount, matching.size(), fileFilter.description(), newDownloads.filesAsString());
      throw new FileNotDownloadedError(message, timeout);
    }

    long deadline = start + timeout;
    return switch (contentStrategy) {
      case FULL_CONTENT -> archiveAll(config, webDriver, matching, deadline);
      case EMPTY_CONTENT -> matching.stream()
        .map(f -> downloader.mockFileContent(config, f.getName()))
        .toList();
    };
  }

  private List<File> archiveAll(Config config, WebDriver webDriver,
                                List<DownloadedFile> downloads, long deadline) {
    File uniqueFolder = downloader.prepareTargetFolder(config);
    List<File> archived = new java.util.ArrayList<>(downloads.size());
    for (DownloadedFile d : downloads) {
      File destination = new File(uniqueFolder, d.getName());
      long remaining = Math.max(1, deadline - currentTimeMillis());
      File copied = downloader.copyFileWithTimeout(d.getName(),
        () -> moveInto(webDriver, d.getFile(), destination), remaining);
      archived.add(copied);
    }
    return archived;
  }

  private File moveInto(WebDriver webDriver, File source, File destination) throws IOException {
    moveFile(source, destination);
    log.debug("Moved the downloaded file {} to {}", source, destination);
    return destination;
  }
```

**3c. Generalize `waitForNewFiles`** — rename to `waitForExpectedFiles`, add `expectedCount` parameter, succeed only when `matching.size() == expectedCount`, fail fast when `matching.size() > expectedCount`:

```java
  void waitForExpectedFiles(Driver driver, FileFilter fileFilter, DownloadsFolder folder,
                            List<DownloadedFile> previousFiles, int expectedCount,
                            long timeout, long incrementTimeout, long pollingInterval) {
    if (log.isDebugEnabled()) {
      log.debug("Waiting for {} matching files in {}...", expectedCount, folder);
    }

    long start = currentTimeMillis();
    for (; currentTimeMillis() - start <= timeout; pause(pollingInterval)) {
      Downloads downloads = new Downloads(folder.filesExcept(previousFiles));
      List<DownloadedFile> matchingFiles = downloads.files(fileFilter);
      if (matchingFiles.size() > expectedCount) {
        String message = String.format(
          "Expected %d files, but found %d new files matching %s: %s",
          expectedCount, matchingFiles.size(), fileFilter.description(), downloads.filesAsString());
        throw new FileNotDownloadedError(message, timeout);
      }
      if (matchingFiles.size() == expectedCount) {
        log.debug("Found {} matching files: {}", expectedCount, matchingFiles);
        return;
      }
      log.debug("Matching files not yet at expected count {}: have {} of {}, all new: {}",
        expectedCount, matchingFiles.size(), expectedCount, downloads.filesAsString());
      failFastIfNoChanges(driver, folder, fileFilter, start, timeout, incrementTimeout);
    }

    log.debug("Stop waiting for {} files after {} ms.",
      expectedCount, currentTimeMillis() - start);
  }
```

**3d. Update the single-file call site** — replace `waitForNewFiles(...)` invocation with `waitForExpectedFiles(driver, fileFilter, folder, previousFiles, 1, timeout, incrementTimeout, pollingInterval)` (since the only existing caller now lives in the new `collect(...)`, this already passes through the parameter — just delete the old `waitForNewFiles` method).

**3e. Remove the old `archiveFile` helper** that did per-file folder preparation. Its callers are gone (`collect` uses `archiveAll`). Or — easier and safer — keep it (it's `protected`, may be subclassed elsewhere) and let `archiveAll` be the new path. Choose based on what compiles cleanly with `maxWarnings = 0`. If unused, Checkstyle will flag it; remove and re-run.

**3f. Imports.** Add to the file:

```java
import com.codeborne.selenide.DownloadFilesOptions;
import java.util.ArrayList;
```

- [ ] **Step 4: Run unit tests to verify they pass**

Run: `./gradlew check --tests DownloadFileToFolderTest`
Expected: PASS — both the existing `tracksForNewFilesInDownloadsFolder` and `filesHasNotBeenUpdatedForMs` still pass, plus the 3 new tests.

- [ ] **Step 5: Run the full unit test suite to catch regressions**

Run: `./gradlew check`
Expected: PASS. No checkstyle or SpotBugs violations.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/codeborne/selenide/impl/DownloadFileToFolder.java \
        src/test/java/com/codeborne/selenide/impl/DownloadFileToFolderTest.java
git commit -m "$(cat <<'EOF'
#3261 generalize DownloadFileToFolder to collect N files

Single-file download() continues to return File; new downloadFiles(...)
returns a sorted List<File> with all N matching new files archived into
one per-call folder. Wait loop fails fast when too many matching files
appear.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 4: Wire `downloadFiles` to `SelenideElement`

**Files:**
- Create: `src/main/java/com/codeborne/selenide/commands/DownloadFiles.java`
- Create: `src/test/java/com/codeborne/selenide/commands/DownloadFilesTest.java`
- Modify: `src/main/java/com/codeborne/selenide/SelenideElement.java`
- Modify: `src/main/java/com/codeborne/selenide/commands/Commands.java`

- [ ] **Step 1: Write the failing unit tests for the command**

Create `src/test/java/com/codeborne/selenide/commands/DownloadFilesTest.java`:

```java
package com.codeborne.selenide.commands;

import com.codeborne.selenide.DownloadFilesOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.CDP;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DownloadFilesTest {
  private final DownloadFileWithHttpRequest httpGet = mock();
  private final DownloadFileWithProxyServer proxy = mock();
  private final DownloadFileToFolder folder = mock();
  private final DownloadFileWithCdp cdp = mock();
  private final DownloadFiles command = new DownloadFiles(httpGet, proxy, folder, cdp);

  private final SelenideElement element = mock();
  private final WebElementSource source = mock();
  private final WebElement webElement = mock();

  // Note: the full integration of source.driver(), config, link wiring is
  // covered indirectly by integration tests; here we keep mocks minimal and
  // focus on dispatch + HTTPGET rejection.

  @Test
  void httpgetRejectsMultiFile() {
    // No need for full source wiring: validation happens before dispatch.
    DownloadFilesOptions options = files(2).withMethod(HTTPGET);

    assertThatThrownBy(() -> command.validateMode(options))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("HTTPGET")
      .hasMessageContaining("FOLDER")
      .hasMessageContaining("CDP")
      .hasMessageContaining("PROXY");
  }

  @Test
  void httpgetAllowsSingleFile() {
    DownloadFilesOptions options = files(1).withMethod(HTTPGET);
    // Should not throw
    command.validateMode(options);
  }

  @Test
  void folderAllowsMultiFile() {
    command.validateMode(files(5).withMethod(FOLDER));
    command.validateMode(files(5).withMethod(CDP));
    command.validateMode(files(5).withMethod(PROXY));
  }
}
```

Note: the dispatch test that walks through `execute(...)` is heavy on wiring (Driver/Config/WebElementSource mocks); the integration tests in Tasks 5–7 exercise dispatch end-to-end. The unit test here pins down the rejection rule alone.

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew check --tests DownloadFilesTest`
Expected: COMPILE FAILURE — class `DownloadFiles` does not exist.

- [ ] **Step 3: Create the `DownloadFiles` command**

Create `src/main/java/com/codeborne/selenide/commands/DownloadFiles.java`:

```java
package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadFilesOptions;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.StopCommandExecutionException;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.impl.DurationFormat.formatDuration;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNullElse;
import static java.util.Optional.ofNullable;

public class DownloadFiles implements Command<List<File>> {
  private static final Logger log = LoggerFactory.getLogger(DownloadFiles.class);

  private final DownloadFileWithHttpRequest downloadFileWithHttpRequest;
  private final DownloadFileWithProxyServer downloadFileWithProxyServer;
  private final DownloadFileToFolder downloadFileToFolder;
  private final DownloadFileWithCdp downloadFileWithCdp;

  public DownloadFiles() {
    this(new DownloadFileWithHttpRequest(), new DownloadFileWithProxyServer(),
      inject(DownloadFileToFolder.class), inject(DownloadFileWithCdp.class));
  }

  DownloadFiles(DownloadFileWithHttpRequest httpGet, DownloadFileWithProxyServer proxy,
                DownloadFileToFolder folder, DownloadFileWithCdp cdp) {
    downloadFileWithHttpRequest = httpGet;
    downloadFileWithProxyServer = proxy;
    downloadFileToFolder = folder;
    downloadFileWithCdp = cdp;
  }

  @Override
  public List<File> execute(SelenideElement selenideElement, WebElementSource linkWithHref, Object @Nullable [] args) {
    if (args == null || args.length == 0 || !(args[0] instanceof DownloadFilesOptions options)) {
      throw new IllegalArgumentException("downloadFiles requires a DownloadFilesOptions argument");
    }
    Config config = linkWithHref.driver().config();
    long timeout = ofNullable(options.timeout()).map(Duration::toMillis).orElse(config.timeout());
    long incrementTimeout = ofNullable(options.incrementTimeout()).map(Duration::toMillis).orElse(timeout);
    if (timeout < incrementTimeout) {
      String error = "Timeout (%s ms) must be greater than increment timeout (%s ms)".formatted(timeout, incrementTimeout);
      throw new IllegalArgumentException(error);
    }

    FileDownloadMode method = requireNonNullElse(options.getMethod(), config.fileDownload());
    DownloadFilesOptions resolved = options.getMethod() == null ? options.withMethod(method) : options;
    validateMode(resolved);

    log.debug("Download {} files with method {}, timeout {}, incTimeout {}",
      resolved.expectedFileCount(), method, formatDuration(timeout), formatDuration(incrementTimeout));

    WebElement link = waitForLink(linkWithHref, incrementTimeout);

    return switch (method) {
      case HTTPGET -> downloadFileWithHttpRequest.downloadFiles(linkWithHref.driver(), link, timeout, resolved);
      case PROXY -> downloadFileWithProxyServer.downloadFiles(linkWithHref, link, timeout, resolved);
      case FOLDER -> downloadFileToFolder.downloadFiles(linkWithHref, link, timeout, incrementTimeout, resolved);
      case CDP -> downloadFileWithCdp.downloadFiles(linkWithHref, link, timeout, incrementTimeout, resolved);
    };
  }

  void validateMode(DownloadFilesOptions options) {
    FileDownloadMode method = options.getMethod();
    if (method == FileDownloadMode.HTTPGET && options.expectedFileCount() > 1) {
      throw new UnsupportedOperationException(
        "HTTPGET mode downloads a single href; use FileDownloadMode.FOLDER, CDP, or PROXY for multi-file downloads");
    }
  }

  private static WebElement waitForLink(WebElementSource linkWithHref, long incrementTimeout) {
    try {
      return linkWithHref.findAndAssertElementIsInteractable();
    }
    catch (ElementNotFound elementNotFound) {
      throw new StopCommandExecutionException(elementNotFound, incrementTimeout);
    }
  }
}
```

Note: this references `downloadFileToFolder.downloadFiles(...)`, `downloadFileWithCdp.downloadFiles(...)`, `downloadFileWithProxyServer.downloadFiles(...)`, and `downloadFileWithHttpRequest.downloadFiles(...)`. Only `DownloadFileToFolder.downloadFiles` exists at this point. Stub the others — they'll be filled in by later tasks. For now, add **stub** methods to the three remaining classes so this task compiles:

In `src/main/java/com/codeborne/selenide/impl/DownloadFileWithCdp.java`, add (will be implemented in Task 6):

```java
public List<File> downloadFiles(WebElementSource link, WebElement clickable,
                                long timeout, long incrementTimeout, DownloadFilesOptions options) {
  throw new UnsupportedOperationException("CDP multi-file download not yet implemented");
}
```

Add imports: `com.codeborne.selenide.DownloadFilesOptions`, `java.util.List`.

In `src/main/java/com/codeborne/selenide/impl/DownloadFileWithProxyServer.java`, add (will be implemented in Task 7):

```java
public List<File> downloadFiles(WebElementSource link, WebElement clickable,
                                long timeout, DownloadFilesOptions options) {
  throw new UnsupportedOperationException("PROXY multi-file download not yet implemented");
}
```

Add imports: `com.codeborne.selenide.DownloadFilesOptions`, `java.util.List`.

In `src/main/java/com/codeborne/selenide/impl/DownloadFileWithHttpRequest.java`, add (will be implemented further in Task 8):

```java
public List<File> downloadFiles(Driver driver, WebElement element, long timeout, DownloadFilesOptions options) {
  if (options.expectedFileCount() > 1) {
    throw new UnsupportedOperationException(
      "HTTPGET mode downloads a single href; use FileDownloadMode.FOLDER, CDP, or PROXY for multi-file downloads");
  }
  return List.of(download(driver, element, timeout, options.getFilter(), options.contentStrategy()));
}
```

Add imports: `com.codeborne.selenide.DownloadFilesOptions`, `java.util.List`.

Also: the existing private `download(Driver, WebElement, long, FileFilter, ContentStrategy)` is private — promote it to package-private (drop `private`) so the new method can call it, or duplicate the small dispatch logic. Recommended: change `private` → no modifier (package-private) for the existing method.

- [ ] **Step 4: Register the command in `Commands.java`**

In `src/main/java/com/codeborne/selenide/commands/Commands.java`, change `addFileCommands()`:

```java
  private void addFileCommands() {
    add("download", new DownloadFile());
    add("downloadFiles", new DownloadFiles());
    add("uploadFile", new UploadFile());
    add("uploadFromClasspath", new UploadFileFromClasspath());
  }
```

- [ ] **Step 5: Add `downloadFiles` to `SelenideElement`**

In `src/main/java/com/codeborne/selenide/SelenideElement.java`, add right after the existing `download(DownloadOptions)` method (around line 1241):

```java
  /**
   * Download multiple files triggered by a single user action on this element.
   *
   * <p>Use this method when a click causes the browser to download more than one file
   * (e.g., an "Export all" button). The number of files to wait for is declared via
   * {@link DownloadFilesOptions#files(int)}.</p>
   *
   * <p>Example:
   * <pre>{@code
   * List<File> reports = $("#exportAll").downloadFiles(
   *     files(3).withExtension("pdf").withTimeout(ofSeconds(30))
   * );
   * }</pre></p>
   *
   * <p>Files are returned in completion order, oldest first.
   * For HTTPGET mode, {@code expectedFileCount > 1} throws
   * {@link UnsupportedOperationException}.</p>
   *
   * @throws FileNotDownloadedError if fewer or more matching files were observed within the timeout
   * @see DownloadFilesOptions#files(int)
   * @see com.codeborne.selenide.commands.DownloadFiles
   */
  List<File> downloadFiles(DownloadFilesOptions options) throws FileNotDownloadedError;
```

Add the import: `import com.codeborne.selenide.DownloadFilesOptions;`. The `java.io.File`, `java.util.List`, and `FileNotDownloadedError` imports should already be present.

- [ ] **Step 6: Run tests to verify they pass**

Run: `./gradlew check --tests DownloadFilesTest`
Expected: PASS, 3 tests green.

Run: `./gradlew check`
Expected: PASS — full unit test suite + Checkstyle + SpotBugs clean.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/codeborne/selenide/SelenideElement.java \
        src/main/java/com/codeborne/selenide/commands/Commands.java \
        src/main/java/com/codeborne/selenide/commands/DownloadFiles.java \
        src/main/java/com/codeborne/selenide/impl/DownloadFileWithCdp.java \
        src/main/java/com/codeborne/selenide/impl/DownloadFileWithProxyServer.java \
        src/main/java/com/codeborne/selenide/impl/DownloadFileWithHttpRequest.java \
        src/test/java/com/codeborne/selenide/commands/DownloadFilesTest.java
git commit -m "$(cat <<'EOF'
#3261 wire downloadFiles to SelenideElement

Add the DownloadFiles command, register it, expose
SelenideElement.downloadFiles(DownloadFilesOptions). Mode-specific
implementations beyond FOLDER are stubbed and filled in by follow-up
commits.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 5: Integration test — FOLDER mode

**Files:**
- Create: `statics/src/test/java/integration/MultiFileDownloadToFolderTest.java`

The existing test page `src/test/resources/downloadMultipleFiles.html` clicks 3 downloads (`download.html`, `empty.html`, `hello_world.txt`).

- [ ] **Step 1: Write the failing integration test**

Create `statics/src/test/java/integration/MultiFileDownloadToFolderTest.java`:

```java
package integration;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MultiFileDownloadToFolderTest extends IntegrationTest {

  @BeforeEach
  void openPage() {
    openFile("downloadMultipleFiles.html");
  }

  @Test
  void downloadsAllThreeFiles() {
    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(3).withMethod(FOLDER).withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).hasSize(3);
    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html", "hello_world.txt");
    assertThat(downloaded.stream().map(File::getParentFile).distinct().toList())
      .as("all files archived into one folder")
      .hasSize(1);
  }

  @Test
  void filterNarrowsCount() {
    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(2).withMethod(FOLDER).withExtension("html").withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html");
  }

  @Test
  void failsFastWhenMoreFilesThanExpected() {
    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(2).withMethod(FOLDER).withTimeout(ofSeconds(10))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files");
  }

  @Test
  void failsWhenFewerFilesThanExpected() {
    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(5).withMethod(FOLDER).withTimeout(ofSeconds(2))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Failed to download 5 files");
  }
}
```

Use the base class that other FOLDER tests in this directory use. Open `statics/src/test/java/integration/FileDownloadToFolderTest.java` line 1 to confirm what they extend (e.g., `IntegrationTest` vs `ITest`) and copy that base class declaration into the new file.

- [ ] **Step 2: Run the test in headless Chrome to verify it fails**

Run: `./gradlew chrome_headless --tests integration.MultiFileDownloadToFolderTest`
Expected: COMPILE FAILURE only if anything from Task 4 didn't land; otherwise tests should PASS already because Tasks 3+4 already implement FOLDER multi-file. If the tests do pass, that's the happy path — proceed to commit.

- [ ] **Step 3: Run the test in headless Firefox**

Run: `./gradlew firefox_headless --tests integration.MultiFileDownloadToFolderTest`
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add statics/src/test/java/integration/MultiFileDownloadToFolderTest.java
git commit -m "$(cat <<'EOF'
#3261 integration test for FOLDER multi-file download

Covers happy path (3 files), extension filter narrowing, fail-fast on too
many files, and fail on too few. Exercises the new downloadFiles(...) end
to end via the Chrome and Firefox headless integration suites.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 6: CDP mode

**Files:**
- Modify: `src/main/java/com/codeborne/selenide/impl/DownloadFileWithCdp.java`
- Create: `statics/src/test/java/integration/MultiFileDownloadWithCdpTest.java`

- [ ] **Step 1: Write the failing CDP integration test**

Create `statics/src/test/java/integration/MultiFileDownloadWithCdpTest.java`. Follow the structure and base class of `statics/src/test/java/integration/FileDownloadToFolderWithCdpTest.java` exactly — same `@BeforeEach`, same Chromium-only tag/assumption.

```java
package integration;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.CDP;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Add any Chromium-only assumeThat / tag imports used by FileDownloadToFolderWithCdpTest.

final class MultiFileDownloadWithCdpTest extends IntegrationTest {

  @BeforeEach
  void openPage() {
    // Copy any Chromium-only assumption from FileDownloadToFolderWithCdpTest.openPage()
    openFile("downloadMultipleFiles.html");
  }

  @Test
  void downloadsAllThreeFiles() {
    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(3).withMethod(CDP).withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).hasSize(3);
    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html", "hello_world.txt");
  }

  @Test
  void failsFastWhenMoreFilesThanExpected() {
    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(2).withMethod(CDP).withTimeout(ofSeconds(10))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files");
  }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew chrome_headless --tests integration.MultiFileDownloadWithCdpTest`
Expected: FAIL with `UnsupportedOperationException("CDP multi-file download not yet implemented")` (the stub from Task 4).

- [ ] **Step 3: Implement `DownloadFileWithCdp.downloadFiles`**

In `src/main/java/com/codeborne/selenide/impl/DownloadFileWithCdp.java`, replace the stub method body with the real implementation. Pattern:

1. Initialize DevTools and listeners exactly as `download(...)` does.
2. Click via `action.perform(driver, clickable)`.
3. Inside `CdpDownloads`, add `find(FileFilter)` → already returns one match. Add a new helper `findAll(FileFilter)` returning `Collection<CdpDownload>` of completed matches. Loop until `findAll(fileFilter).size() == expectedFileCount` (or timeout / no-progress).
4. Fail fast when `findAll(fileFilter).size() > expectedFileCount`.
5. Archive all matched files into one folder via the same helper pattern as `DownloadFileToFolder.archiveAll` (replicate locally; do **not** add a cross-class helper just for this).
6. Return sorted by file `lastModified()` ascending, name tiebreaker.

Concrete:

```java
public List<File> downloadFiles(WebElementSource link, WebElement clickable,
                                long timeout, long incrementTimeout, DownloadFilesOptions options) {
  long start = currentTimeMillis();
  Driver driver = link.driver();
  Config config = driver.config();
  WebDriver webDriver = driver.getWebDriver();
  DevTools devTools = initDevTools(driver);
  DownloadsFolder downloadsFolder = requireNonNull(getDownloadsFolder(driver), "Webdriver downloads folder is not configured");
  CdpDownloads downloads = new CdpDownloads(downloadsFolder, new ConcurrentHashMap<>(1));

  prepareDownloadWithCdp(driver, devTools, downloads, timeout);
  options.getAction().perform(driver, clickable);

  try {
    List<CdpDownload> matched = waitUntilExpectedDownloadsCompleted(driver,
      options.getFilter(), options.expectedFileCount(), timeout, incrementTimeout, downloads);

    long deadline = start + timeout;
    return switch (options.contentStrategy()) {
      case FULL_CONTENT -> archiveAllCdp(config, matched, deadline);
      case EMPTY_CONTENT -> matched.stream()
        .map(d -> downloader.mockFileContent(config, requireNonNull(d.fileName)))
        .toList();
    };
  }
  finally {
    devTools.clearListeners();
  }
}

private List<File> archiveAllCdp(Config config, List<CdpDownload> downloads, long deadline) {
  File uniqueFolder = downloader.prepareTargetFolder(config);
  List<File> archived = new ArrayList<>(downloads.size());
  for (CdpDownload d : downloads) {
    File destination = new File(uniqueFolder, d.file().getName());
    long remaining = Math.max(1, deadline - currentTimeMillis());
    File copied = downloader.copyFileWithTimeout(d.file().getName(),
      () -> { moveFile(d.file(), destination); return destination; }, remaining);
    archived.add(copied);
  }
  archived.sort(Comparator.comparingLong(File::lastModified).thenComparing(File::getName));
  return archived;
}

private List<CdpDownload> waitUntilExpectedDownloadsCompleted(Driver driver, FileFilter fileFilter,
                                                              int expectedCount,
                                                              long timeout, long incrementTimeout,
                                                              CdpDownloads downloads) {
  long pollingInterval = Math.max(driver.config().pollingInterval(), 100);
  long downloadStartedAt = currentTimeMillis();
  Stopwatch stopwatch = new Stopwatch(timeout);
  do {
    List<CdpDownload> completed = downloads.findAll(fileFilter);
    if (completed.size() > expectedCount) {
      String message = String.format("Expected %d files, but found %d new files matching %s",
        expectedCount, completed.size(), fileFilter.description());
      throw new FileNotDownloadedError(message, timeout);
    }
    if (completed.size() == expectedCount) {
      return completed;
    }
    failFastIfNoChanges(downloads, fileFilter, downloadStartedAt, timeout, incrementTimeout);
    stopwatch.sleep(pollingInterval);
  }
  while (!stopwatch.isTimeoutReached());

  throw new FileNotDownloadedError(
    "Failed to download %d files in %s, found files: %s".formatted(
      expectedCount, df.format(timeout), downloads.folder().files()),
    timeout);
}
```

And in the private `CdpDownloads` record, add:

```java
private List<CdpDownload> findAll(FileFilter fileFilter) {
  return downloads.values().stream()
    .filter(download -> download.completed)
    .filter(download -> fileFilter.match(download.file()))
    .toList();
}
```

Add imports as needed: `java.util.ArrayList`, `java.util.Comparator`, `java.util.List`.

- [ ] **Step 4: Run the unit test suite for regressions**

Run: `./gradlew check`
Expected: PASS.

- [ ] **Step 5: Run the CDP integration test**

Run: `./gradlew chrome_headless --tests integration.MultiFileDownloadWithCdpTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/codeborne/selenide/impl/DownloadFileWithCdp.java \
        statics/src/test/java/integration/MultiFileDownloadWithCdpTest.java
git commit -m "$(cat <<'EOF'
#3261 implement CDP multi-file download

Wait until exactly expectedFileCount CDP downloads complete, archive into
one folder, sort by mtime asc. Fail fast on too many.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 7: PROXY mode

**Files:**
- Modify: `src/main/java/com/codeborne/selenide/impl/DownloadFileWithProxyServer.java`
- Create: `modules/proxy/src/test/java/integration/proxy/MultiFileDownloadViaProxyTest.java`

- [ ] **Step 1: Write the failing PROXY integration test**

Create `modules/proxy/src/test/java/integration/proxy/MultiFileDownloadViaProxyTest.java`. Mirror the structure of `modules/proxy/src/test/java/integration/proxy/MultipleDownloadsTest.java` (which exists today), using `ProxyIntegrationTest` as the base class.

```java
package integration.proxy;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MultiFileDownloadViaProxyTest extends ProxyIntegrationTest {

  @Test
  void downloadsAllThreeFiles() {
    openFile("downloadMultipleFiles.html");

    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(3).withMethod(PROXY).withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html", "hello_world.txt");
  }

  @Test
  void failsFastWhenMoreFilesThanExpected() {
    openFile("downloadMultipleFiles.html");

    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(2).withMethod(PROXY).withTimeout(ofSeconds(10))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files");
  }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :modules:proxy:chrome_headless --tests integration.proxy.MultiFileDownloadViaProxyTest`
Expected: FAIL with `UnsupportedOperationException("PROXY multi-file download not yet implemented")`.

If the proxy module uses a different gradle task name, check `modules/proxy/build.gradle`.

- [ ] **Step 3: Implement `DownloadFileWithProxyServer.downloadFiles`**

In `src/main/java/com/codeborne/selenide/impl/DownloadFileWithProxyServer.java`, replace the stub method body. Pattern:

1. Same proxy / filter setup as `download(...)`.
2. Click via `options.getAction().perform(driver, clickable)`.
3. Wait until `filter.downloads().matchingFiles(filter).size() == expectedFileCount`. Fail fast if `> expectedFileCount`.
4. Each `DownloadedFile` in the proxy filter has an associated File already on disk. Return those Files sorted by mtime asc with name tiebreaker.

Concrete implementation:

```java
public List<File> downloadFiles(WebElementSource link, WebElement clickable,
                                long timeout, DownloadFilesOptions options) {
  Driver driver = link.driver();
  Config config = driver.config();
  if (!config.proxyEnabled()) {
    throw new IllegalStateException("Cannot download file: proxy server is not enabled. Setup proxyEnabled");
  }

  SelenideProxyServer proxyServer = driver.getProxy();
  FileDownloadFilter filter = proxyServer.responseFilter(SELENIDE_PROXY_FILTER_PREFIX + "download");
  if (filter == null) {
    throw new IllegalStateException("Cannot download file: download filter is not activated");
  }

  filter.activate(options.contentStrategy());
  try {
    long pollingInterval = Math.max(config.pollingInterval(), 50);
    waitForPreviousDownloadsCompletion(filter, timeout, pollingInterval);

    filter.reset();
    options.getAction().perform(driver, clickable);

    waitForExpectedDownloads(filter, options.getFilter(), options.expectedFileCount(),
      timeout, pollingInterval);

    if (log.isInfoEnabled()) {
      log.info("Downloaded {}", filter.downloads().filesAsString());
      log.info("Just in case, intercepted {}", filter.responsesAsString());
    }

    List<DownloadedFile> matching = filter.downloads().matchingFiles(options.getFilter());
    if (matching.size() > options.expectedFileCount()) {
      throw new FileNotDownloadedError(
        "Expected %d files, but found %d new files matching %s: %s".formatted(
          options.expectedFileCount(), matching.size(),
          options.getFilter().description(), filter.downloads().filesAsString()),
        timeout);
    }
    if (matching.size() < options.expectedFileCount()) {
      throw new FileNotDownloadedError(
        "Failed to download %d files: only %d matched %s".formatted(
          options.expectedFileCount(), matching.size(), options.getFilter().description()),
        timeout);
    }
    return matching.stream().map(DownloadedFile::getFile).toList();
  }
  finally {
    filter.deactivate();
  }
}

private void waitForExpectedDownloads(FileDownloadFilter filter, FileFilter fileFilter,
                                      int expectedCount, long timeout, long pollingInterval) {
  waiter.wait(timeout, pollingInterval, () -> {
    int count = filter.downloads().matchingFiles(fileFilter).size();
    if (count > expectedCount) {
      throw new FileNotDownloadedError(
        "Expected %d files, but found %d new files matching %s".formatted(
          expectedCount, count, fileFilter.description()),
        timeout);
    }
    return count == expectedCount;
  });
}
```

Add imports: `com.codeborne.selenide.DownloadFilesOptions`, `com.codeborne.selenide.ex.FileNotDownloadedError`, `com.codeborne.selenide.files.DownloadedFile`, `java.util.List`.

Note: `matchingFiles` on the proxy `Downloads` returns sorted-by-mtime; for proxy this is good enough (the file mtime corresponds to when the proxy wrote the response to disk).

- [ ] **Step 4: Run the proxy integration test**

Run: `./gradlew :modules:proxy:chrome_headless --tests integration.proxy.MultiFileDownloadViaProxyTest`
Expected: PASS.

- [ ] **Step 5: Run the full check suite**

Run: `./gradlew check`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/codeborne/selenide/impl/DownloadFileWithProxyServer.java \
        modules/proxy/src/test/java/integration/proxy/MultiFileDownloadViaProxyTest.java
git commit -m "$(cat <<'EOF'
#3261 implement PROXY multi-file download

Wait for N matching captured responses, fail fast on too many, return File
list sorted by mtime asc.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 8: HTTPGET rejection — finalize

**Files:**
- Modify: `src/main/java/com/codeborne/selenide/impl/DownloadFileWithHttpRequest.java`
- Already covered by `DownloadFilesTest.httpgetRejectsMultiFile()` from Task 4.

The stub from Task 4 already does the right thing (rejects N > 1, passes N == 1 to single-file path). Verify nothing else is needed:

- [ ] **Step 1: Re-read the stub and confirm behavior**

Confirm `DownloadFileWithHttpRequest.downloadFiles` rejects `expectedFileCount > 1` with the exact spec message, and `== 1` delegates to the existing private `download(...)`. If the private `download` is still `private`, promote it to package-private so the new method can call it (this should already be done in Task 4 step 3; re-check).

- [ ] **Step 2: Add a regression integration test for HTTPGET single-file via downloadFiles**

Optional but recommended. In `statics/src/test/java/integration/FileDownloadViaHttpGetTest.java`, add:

```java
  @Test
  void downloadFilesWithExpectedCountOneWorks() {
    List<File> downloaded = $(byText("Download me")).downloadFiles(
      files(1).withMethod(HTTPGET).withTimeout(ofSeconds(10))
    );
    assertThat(downloaded).hasSize(1);
  }

  @Test
  void downloadFilesWithMultiFileFailsOnHttpget() {
    assertThatThrownBy(() ->
      $(byText("Download me")).downloadFiles(
        files(2).withMethod(HTTPGET).withTimeout(ofSeconds(10))
      )
    ).isInstanceOf(UnsupportedOperationException.class)
     .hasMessageContaining("HTTPGET");
  }
```

Adapt the existing selector and base setup to match the file's existing tests (open `FileDownloadViaHttpGetTest.java` first). Add the matching imports.

- [ ] **Step 3: Run the relevant integration test**

Run: `./gradlew chrome_headless --tests integration.FileDownloadViaHttpGetTest`
Expected: PASS.

- [ ] **Step 4: Commit (only if Step 2 added tests)**

```bash
git add statics/src/test/java/integration/FileDownloadViaHttpGetTest.java
git commit -m "$(cat <<'EOF'
#3261 HTTPGET regression tests for downloadFiles

Cover the single-file passthrough and the multi-file rejection.

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

---

## Task 9: Final gates

- [ ] **Step 1: javadocForSite must be green** (CLAUDE.md gate)

Run: `./gradlew javadocForSite`
Expected: BUILD SUCCESSFUL. If broken, fix javadoc warnings — usually a missing `@param` or unresolved `{@link}` tag in `SelenideElement.downloadFiles` or `DownloadFilesOptions`.

- [ ] **Step 2: Full suite**

Run: `./gradlew check chrome_headless firefox_headless`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: If any fixes were needed in Step 1 or 2, commit them**

```bash
git status
# If there are pending fixes:
git add <fixed files>
git commit -m "$(cat <<'EOF'
#3261 fix javadoc / final polish

Co-Authored-By: Claude Opus 4.7 (1M context) <noreply@anthropic.com>
EOF
)"
```

- [ ] **Step 4: Sanity check the public surface**

Read these files top-to-bottom and confirm they match the spec:
- `src/main/java/com/codeborne/selenide/DownloadFilesOptions.java`
- `src/main/java/com/codeborne/selenide/SelenideElement.java` (the new `downloadFiles` method + its javadoc)
- `src/main/java/com/codeborne/selenide/commands/DownloadFiles.java`

Confirm:
- `DownloadOptions` is unchanged (`git diff main -- src/main/java/com/codeborne/selenide/DownloadOptions.java` should be empty).
- No `withExpectedFileCount` exists anywhere in the source tree.

```bash
git diff main -- src/main/java/com/codeborne/selenide/DownloadOptions.java
grep -rn "withExpectedFileCount" src/ modules/ statics/
```

Expected: no `DownloadOptions` diff; no `withExpectedFileCount` hits.

---

## Done criteria

- All 9 tasks committed on `feat/3261-multi-file-download`.
- `./gradlew check chrome_headless firefox_headless javadocForSite` all green.
- `SelenideElement.downloadFiles(DownloadFilesOptions)` works in FOLDER, CDP, PROXY modes; HTTPGET rejects N > 1.
- `DownloadOptions` is untouched.
