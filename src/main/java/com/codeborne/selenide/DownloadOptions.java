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
  private final FileFilter filter;
  private final DownloadAction action;

  private DownloadOptions(FileDownloadMode method, @Nullable Duration timeout, FileFilter filter, DownloadAction action) {
    this.method = method;
    this.timeout = timeout;
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
    return new DownloadOptions(method, Duration.ofMillis(timeoutMs), filter, action);
  }

  @CheckReturnValue
  @Nonnull
  public DownloadOptions withTimeout(Duration timeout) {
    return new DownloadOptions(method, timeout, filter, action);
  }

  @CheckReturnValue
  @Nonnull
  public DownloadOptions withFilter(FileFilter filter) {
    return new DownloadOptions(method, timeout, filter, action);
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
    return new DownloadOptions(method, timeout, filter, action);
  }

  @Override
  public String toString() {
    if (timeout != null && !filter.isEmpty())
      return String.format("method: %s, timeout: %s ms, filter: %s", method, timeout.toMillis(), filter.description());
    else if (timeout != null)
      return String.format("method: %s, timeout: %s ms", method, timeout.toMillis());
    else if (!filter.isEmpty())
      return String.format("method: %s, filter: %s", method, filter.description());
    else
      return String.format("method: %s", method);
  }

  @CheckReturnValue
  @Nonnull
  public static DownloadOptions using(FileDownloadMode method) {
    return new DownloadOptions(method, null, none(), click());
  }
}
