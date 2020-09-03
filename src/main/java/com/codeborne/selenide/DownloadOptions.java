package com.codeborne.selenide;

import com.codeborne.selenide.files.FileFilter;

import static com.codeborne.selenide.files.FileFilters.none;

public class DownloadOptions {
  private static final long UNSPECIFIED_TIMEOUT = Long.MIN_VALUE;

  private final FileDownloadMode method;
  private final long timeout;
  private final FileFilter filter;

  private DownloadOptions(FileDownloadMode method, long timeout, FileFilter filter) {
    this.method = method;
    this.timeout = timeout;
    this.filter = filter;
  }

  public FileDownloadMode getMethod() {
    return method;
  }

  public long getTimeout(long defaultValue) {
    return timeout == UNSPECIFIED_TIMEOUT ? defaultValue : timeout;
  }

  public FileFilter getFilter() {
    return filter;
  }

  public DownloadOptions withTimeout(long timeout) {
    return new DownloadOptions(method, timeout, filter);
  }

  public DownloadOptions withFilter(FileFilter filter) {
    return new DownloadOptions(method, timeout, filter);
  }

  @Override
  public String toString() {
    return String.format("method=%s, timeout=%s ms, filter='%s'", method, timeout, filter.description());
  }

  public static DownloadOptions using(FileDownloadMode method) {
    return new DownloadOptions(method, UNSPECIFIED_TIMEOUT, none());
  }
}
