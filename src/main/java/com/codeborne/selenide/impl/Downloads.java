package com.codeborne.selenide.impl;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

public class Downloads {
  private static final DurationFormat df = new DurationFormat();
  private final List<DownloadedFile> files = new CopyOnWriteArrayList<>();

  public Downloads() {
  }

  public Downloads(List<DownloadedFile> files) {
    this.files.addAll(files);
  }

  public void clear() {
    files.clear();
  }

  public void add(DownloadedFile file) {
    files.add(file);
  }

  public List<DownloadedFile> files() {
    return files;
  }

  public List<DownloadedFile> files(FileFilter fileFilter) {
    return files.stream().filter(fileFilter::match).collect(toList());
  }

  @Deprecated
  public List<DownloadedFile> matchingFiles(FileFilter fileFilter) {
    return findMatchingFiles(fileFilter).toList();
  }

  @Deprecated
  public Optional<DownloadedFile> firstMatchingFile(FileFilter fileFilter) {
    return findMatchingFiles(fileFilter).findFirst();
  }

  private Stream<DownloadedFile> findMatchingFiles(FileFilter fileFilter) {
    return files.stream()
      .filter(fileFilter::match)
      .sorted(new DownloadDetector());
  }

  public String filesAsString() {
    if (files.isEmpty()) {
      return "[]";
    }
    if (files.size() == 1) {
      return files.get(0) + " (1 file at " + currentTimeMillis() + ")";
    }

    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (DownloadedFile file : files) {
      sb.append("\n  #").append(++i).append("  ").append(file);
    }
    sb.append("\n  (").append(files.size()).append(" files at ").append(currentTimeMillis()).append(")\n");
    return sb.toString();
  }

  public int size() {
    return files.size();
  }

  public List<File> getMatchingDownloadedFiles(long timeout, FileFilter fileFilter, int minimumFileCount) {
    List<DownloadedFile> matchingFiles = findMatchingFiles(fileFilter).toList();
    if (matchingFiles.size() >= minimumFileCount) {
      return matchingFiles.stream().map(DownloadedFile::getFile).toList();
    }

    switch (minimumFileCount) {
      case 1 -> {
        String message = String.format("Failed to download file%s in %s (found %s files: %s)",
          fileFilter.description(), df.format(timeout), files.size(), files);
        throw new FileNotDownloadedError(message.trim(), timeout);
      }
      default -> {
        String message = String.format("Failed to download at least %s files%s in %s (found %s files: %s)",
          minimumFileCount, fileFilter.description(), df.format(timeout), files.size(), files);
        throw new FileNotDownloadedError(message.trim(), timeout);
      }
    }
  }
}
