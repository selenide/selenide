package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public interface DownloadsFolder {
  List<DownloadedFile> files();

  default List<DownloadedFile> filesExcept(List<DownloadedFile> previousFiles) {
    Collection<String> previousFilenames = previousFiles.stream().map(DownloadedFile::getName).collect(toSet());
    List<DownloadedFile> files = files();
    return files.stream()
      .filter(file -> !previousFilenames.contains(file.getName()))
      .toList();
  }

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
