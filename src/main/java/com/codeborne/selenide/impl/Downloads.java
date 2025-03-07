package com.codeborne.selenide.impl;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

public class Downloads {
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
        String message = String.format("Failed to download file%s in %d ms.", fileFilter.description(), timeout);
          return new FileNotDownloadedError(message.trim(), timeout);
        }
      ).getFile();
  }
}
