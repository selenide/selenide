package com.codeborne.selenide.impl;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

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

  /**
   * Returns all files matching the filter, in insertion order.
   * For sorted-by-completion-time results, use {@link #matchingFiles(FileFilter)}.
   */
  public List<DownloadedFile> files(FileFilter fileFilter) {
    return files.stream().filter(fileFilter::match).collect(toList());
  }

  /**
   * Returns all files matching the filter, sorted in completion order
   * ({@link DownloadedFile#lastModifiedTime() lastModifiedTime} ascending,
   * with {@link DownloadedFile#getName() name} as the tiebreaker).
   */
  public List<DownloadedFile> matchingFiles(FileFilter fileFilter) {
    return files.stream()
      .filter(fileFilter::match)
      .sorted(Comparator
        .comparingLong(DownloadedFile::lastModifiedTime)
        .thenComparing(DownloadedFile::getName))
      .collect(toList());
  }

  public Optional<DownloadedFile> firstMatchingFile(FileFilter fileFilter) {
    return files.stream().filter(fileFilter::match).sorted(new DownloadDetector()).findFirst();
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

  public File firstDownloadedFile(long timeout, FileFilter fileFilter) {
    return firstMatchingFile(fileFilter)
      .orElseThrow(() -> {
        String message = String.format("Failed to download file%s in %s", fileFilter.description(), df.format(timeout));
          return new FileNotDownloadedError(message.trim(), timeout);
        }
      ).getFile();
  }
}
