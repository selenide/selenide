package com.codeborne.selenide.files;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class DownloadedFile {
  private final File file;
  private final Map<String, String> headers;

  /**
   * @param file the downloaded file
   * @param headers map of http headers. NB! Map keys (header names) are LOWER CASE!
   */
  public DownloadedFile(File file, Map<String, String> headers) {
    this.file = file;
    this.headers = headers;
  }

  public File getFile() {
    return file;
  }

  public boolean hasContentDispositionHeader() {
    return headers.containsKey("content-disposition");
  }

  @Nullable
  public String getContentType() {
    return headers.get("content-type");
  }

  @Override
  public String toString() {
    return String.format("%s (modified %s ms ago)", file.getName(), currentTimeMillis() - file.lastModified());
  }
}
