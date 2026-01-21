package com.codeborne.selenide.files;

import com.codeborne.selenide.impl.DurationFormat;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.nio.file.Files.readAttributes;
import static java.util.Collections.emptyMap;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class DownloadedFile {
  private static final Logger log = LoggerFactory.getLogger(DownloadedFile.class);
  private static final DurationFormat df = new DurationFormat();
  private static final long BEGINNING_OF_TIME = Instant.parse("1997-08-29T02:14:00-07:00").toEpochMilli();

  private final File file;
  private final long lastModifiedTime;
  private final long size;
  private final Map<String, String> headers;

  /**
   * @param file    the downloaded file
   * @param headers map of http headers. NB! Map keys (header names) are LOWER CASE!
   */
  public DownloadedFile(File file, long lastModifiedTime, long size, Map<String, String> headers) {
    this.file = file;
    this.lastModifiedTime = lastModifiedTime;
    this.size = size;
    this.headers = headers;
  }

  public File getFile() {
    return file;
  }

  public String getName() {
    return file.getName();
  }

  public String extension() {
    return getExtension(file.getName());
  }

  public long size() {
    return size;
  }

  public boolean hasContentDispositionHeader() {
    return headers.containsKey("content-disposition");
  }

  @Nullable
  public String getContentType() {
    return headers.get("content-type");
  }

  public long lastModifiedTime() {
    return lastModifiedTime;
  }

  /**
   * Depending on OS, file modification time can have seconds precision, not milliseconds.
   * We have to ignore the difference in milliseconds.
   */
  public boolean isFileModifiedLaterThan(long timestamp) {
    return lastModifiedTime - timestamp >= -1000L;
  }

  /**
   * May be inaccurate for file on remote webdriver
   * (and it's system time differs from the system time on test machine).
   */
  @Override
  public String toString() {
    return isModificationTimeKnown() ?
      String.format("%s (modified %s ago)", file.getName(), df.format(currentTimeMillis() - lastModifiedTime)) :
      file.getName();
  }

  private boolean isModificationTimeKnown() {
    return lastModifiedTime > BEGINNING_OF_TIME;
  }

  public static DownloadedFile fileWithName(String fileName) {
    return new DownloadedFile(new File(fileName), 0, 0, emptyMap());
  }

  public static DownloadedFile localFile(File file) {
    try {
      BasicFileAttributes attributes = readAttributes(file.toPath(), BasicFileAttributes.class);
      return new DownloadedFile(file, attributes.lastModifiedTime().toMillis(),
        attributes.size(), emptyMap());
    }
    catch (IOException e) {
      log.debug("Failed to get file attributes: {}", file.getAbsolutePath(), e);
      return new DownloadedFile(file, 0, 0, emptyMap());
    }
  }
}
