package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.io.FilenameUtils.getExtension;

@ParametersAreNonnullByDefault
public abstract class DownloadsFolder {
  protected final File folder;

  protected DownloadsFolder(File folder) {
    this.folder = folder.getAbsoluteFile();
  }

  @CheckReturnValue
  @Nonnull
  public File toFile() {
    return folder;
  }

  @CheckReturnValue
  @Nonnull
  public List<File> files() {
    File[] files = folder.listFiles();
    return files == null ? emptyList() : asList(files);
  }

  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files().stream()
      .filter(File::isFile)
      .filter(file -> isFileModifiedLaterThan(file, modifiedAfterTs))
      .map(file -> new DownloadedFile(file, emptyMap()))
      .collect(toList());
  }

  /**
   * Depending on OS, file modification time can have seconds precision, not milliseconds.
   * We have to ignore the difference in milliseconds.
   */
  static boolean isFileModifiedLaterThan(File file, long timestamp) {
    return file.lastModified() - timestamp >= -1000L;
  }

  public abstract void cleanupBeforeDownload();

  public abstract void deleteIfEmpty();

  @CheckReturnValue
  @Nonnull
  public File file(String fileName) {
    return new File(folder, fileName).getAbsoluteFile();
  }

  public boolean hasFiles(Set<String> extensions, FileFilter excludingFilter) {
    return files().stream()
      .anyMatch(file -> extensions.contains(getExtension(file.getName()).toLowerCase(ROOT)) && excludingFilter.notMatch(file));
  }

  public Map<String, Long> modificationTimes() {
    File[] files = folder.listFiles();
    return files == null ? emptyMap() : Stream.of(files).collect(toMap(f -> f.getName(), f -> f.lastModified()));
  }

  public Optional<Long> lastModificationTime() {
    return modificationTimes().values().stream().max(Long::compare);
  }

  @Override
  public String toString() {
    return folder.getPath();
  }

  public String filesAsString() {
    return '[' + files().stream().map(f -> f.getName()).collect(joining(", ")) + ']';
  }
}
