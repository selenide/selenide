package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public interface DownloadsFolder {
  List<DownloadedFile> files();

  List<DownloadedFile> filesNewerThan(long modifiedAfterTs);

  void cleanupBeforeDownload();

  void deleteIfEmpty();

  default boolean hasFiles(Set<String> extensions, FileFilter excludingFilter) {
    return files().stream()
      .anyMatch(file -> extensions.contains(file.extension().toLowerCase(ROOT)) && excludingFilter.notMatch(file.getFile()));
  }

  default Map<String, Long> modificationTimes() {
    return files().stream().collect(toMap(f -> f.getName(), f -> f.lastModifiedTime()));
  }

  default Optional<Long> lastModificationTime() {
    return modificationTimes().values().stream().max(Long::compare);
  }

  default String filesAsString() {
    return '[' + files().stream().map(f -> f.getName()).collect(joining(", ")) + ']';
  }

  String getPath();
}
