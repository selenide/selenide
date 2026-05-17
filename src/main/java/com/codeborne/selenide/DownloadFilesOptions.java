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
