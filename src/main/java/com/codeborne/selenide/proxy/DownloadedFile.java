package com.codeborne.selenide.proxy;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Map;

@ParametersAreNonnullByDefault
public class DownloadedFile {
  private final File file;
  private final long lastModifiedAt;
  private final Map<String, String> headers;

  /**
   * @param file the downloaded file
   * @param headers map of http headers. NB! Map keys (header names) are LOWER CASE!
   */
  public DownloadedFile(File file, Map<String, String> headers) {
    this.file = file;
    this.lastModifiedAt = file.lastModified();
    this.headers = headers;
  }

  @CheckReturnValue
  @Nonnull
  public File getFile() {
    return file;
  }

  @CheckReturnValue
  public boolean hasContentDispositionHeader() {
    return headers.containsKey("content-disposition");
  }

  @Nullable
  public String getContentType() {
    return headers.get("content-type");
  }

  @Override
  public int hashCode() {
    return 31 * file.hashCode() + Long.hashCode(lastModifiedAt);
  }

  public boolean equals(Object obj) {
    return obj instanceof DownloadedFile && equals((DownloadedFile) obj);
  }

  private boolean equals(DownloadedFile other) {
    return file.equals(other.file) && lastModifiedAt == other.lastModifiedAt;
  }
}
