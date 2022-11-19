package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.HasTimeout;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.files.DownloadActions.click;
import static com.codeborne.selenide.files.FileFilters.none;

@ParametersAreNonnullByDefault
public class DownloadOptions implements HasTimeout {
  private final FileDownloadMode method;
  @Nullable
  private final Duration timeout;
  @Nullable
  private final Duration incrementTimeout;
  private final FileFilter filter;
  private final DownloadAction action;

  private DownloadOptions(FileDownloadMode method, @Nullable Duration timeout, @Nullable Duration incrementTimeout,
                          FileFilter filter, DownloadAction action) {
    this.method = method;
    this.timeout = timeout;
    this.incrementTimeout = incrementTimeout;
    this.filter = filter;
    this.action = action;
  }

  @CheckReturnValue
  @Nonnull
  public FileDownloadMode getMethod() {
    return method;
  }

  @CheckReturnValue
  @Override
  public Duration timeout() {
    return timeout;
  }

  @CheckReturnValue
  public Duration incrementTimeout() {
    return incrementTimeout;
  }

  @CheckReturnValue
  @Nonnull
  public FileFilter getFilter() {
    return filter;
  }

  @CheckReturnValue
  @Nonnull
  public DownloadAction getAction() {
    return action;
  }

  @CheckReturnValue
  @Nonnull
  public DownloadOptions withTimeout(long timeoutMs) {
    return new DownloadOptions(method, Duration.ofMillis(timeoutMs), incrementTimeout, filter, action);
  }

  @CheckReturnValue
  @Nonnull
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
  @CheckReturnValue
  @Nonnull
  public DownloadOptions withIncrementTimeout(Duration incrementTimeout) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  @CheckReturnValue
  @Nonnull
  public DownloadOptions withFilter(FileFilter filter) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  /**
   * User action to start the downloading process.
   * By default, it's a click.
   * <p>
   * Use this method if you need to close some alert before downloading file etc.
   *
   * @param action any lambda accepting a Driver and WebElement (the element being clicked).
   * @return DownloadOptions
   * @since 5.22.0
   */
  public DownloadOptions withAction(DownloadAction action) {
    return new DownloadOptions(method, timeout, incrementTimeout, filter, action);
  }

  @Override
  public String toString() {
    if (timeout != null && !filter.isEmpty())
      return String.format("method: %s, timeout: %s ms, filter:%s", method, timeout.toMillis(), filter.description());
    else if (timeout != null)
      return String.format("method: %s, timeout: %s ms", method, timeout.toMillis());
    else if (!filter.isEmpty())
      return String.format("method: %s, filter:%s", method, filter.description());
    else
      return String.format("method: %s", method);
  }

  @CheckReturnValue
  @Nonnull
  public static DownloadOptions using(FileDownloadMode method) {
    return new DownloadOptions(method, null, null, none(), click());
  }
}
