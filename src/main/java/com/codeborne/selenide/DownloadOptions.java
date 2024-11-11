package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.impl.HasTimeout;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.stream.Stream;

import static com.codeborne.selenide.files.DownloadActions.click;
import static com.codeborne.selenide.files.FileFilters.none;
import static java.util.stream.Collectors.joining;

public class DownloadOptions implements HasTimeout {
  @Nullable
  private final FileDownloadMode method;
  @Nullable
  private final Duration timeout;
  @Nullable
  private final Duration incrementTimeout;
  private final FileFilter filter;
  private final DownloadAction action;

  private DownloadOptions(@Nullable FileDownloadMode method, @Nullable Duration timeout, @Nullable Duration incrementTimeout,
                          FileFilter filter, DownloadAction action) {
    this.method = method;
    this.timeout = timeout;
    this.incrementTimeout = incrementTimeout;
    this.filter = filter;
    this.action = action;
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

  public DownloadOptions withMethod(FileDownloadMode method) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  public DownloadOptions withTimeout(long timeoutMs) {
    return new DownloadOptions(method, Duration.ofMillis(timeoutMs), incrementTimeout, filter, action);
  }

  public DownloadOptions withTimeout(Duration timeout) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  /**
   * Set increment timeout for downloading.
   * If no changes in files will be detected during this period of time, the download will be concluded as failed.
   *
   * <p>
   * Currently, it's used only for FOLDER download method. It's reasonable to set increment timeout when
   * 1. the download timeout is quite large (download might be very slow), and
   * 2. there is a risk that the downloading process hasn't even started (click missed the link etc.)
   *
   * Then setting a shorter increment timeout allows you to fail faster.
   * </p>
   *
   * @param incrementTimeout should be lesser than download timeout
   */
  public DownloadOptions withIncrementTimeout(Duration incrementTimeout) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  public DownloadOptions withFilter(FileFilter filter) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  public DownloadOptions withExtension(String extension) {
    return new DownloadOptions(method, timeout, incrementTimeout, FileFilters.withExtension(extension), action);
  }

  public DownloadOptions withName(String fileName) {
    return new DownloadOptions(method, timeout, incrementTimeout, FileFilters.withName(fileName), action);
  }

  public DownloadOptions withNameMatching(String fileNameRegex) {
    return new DownloadOptions(method, timeout, incrementTimeout, FileFilters.withNameMatching(fileNameRegex), action);
  }

  /**
   * User action to start the downloading process.
   * By default, it's a click.
   * <p>
   * Use this method if you need to close some alert before downloading file etc.
   *
   * @param action any lambda accepting a Driver and WebElement (the element being clicked).
   * @return DownloadOptions
   */
  public DownloadOptions withAction(DownloadAction action) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  @Override
  public String toString() {
    return Stream.of(
        method == null ? null : "method: %s".formatted(method.name()),
        timeout == null ? null : "timeout: %s ms".formatted(timeout.toMillis()),
        incrementTimeout == null ? null : "incrementTimeout: %s ms".formatted(incrementTimeout.toMillis()),
        filter.isEmpty() ? null : filter.toString()
      ).filter(p -> p != null)
      .collect(joining(", "));
  }

  public static DownloadOptions file() {
    return new DownloadOptions(null, null, null, none(), click());
  }

  public static DownloadOptions using(FileDownloadMode method) {
    return file().withMethod(method);
  }
}
