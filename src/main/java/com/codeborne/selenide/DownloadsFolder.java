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

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.io.FilenameUtils.getExtension;

@ParametersAreNonnullByDefault
public interface DownloadsFolder {
  @CheckReturnValue
  @Nonnull
  List<File> files();

  @CheckReturnValue
  @Nonnull
  List<DownloadedFile> filesNewerThan(long modifiedAfterTs);

  void cleanupBeforeDownload();

  void deleteIfEmpty();

  default boolean hasFiles(Set<String> extensions, FileFilter excludingFilter) {
    return files().stream()
      .anyMatch(file -> extensions.contains(getExtension(file.getName()).toLowerCase(ROOT)) && excludingFilter.notMatch(file));
  }

  default Map<String, Long> modificationTimes() {
    return files().stream().collect(toMap(f -> f.getName(), f -> f.lastModified()));
  }

  default Optional<Long> lastModificationTime() {
    return modificationTimes().values().stream().max(Long::compare);
  }

  default String filesAsString() {
    return '[' + files().stream().map(f -> f.getName()).collect(joining(", ")) + ']';
  }
}
