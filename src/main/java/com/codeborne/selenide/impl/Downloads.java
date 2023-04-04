package com.codeborne.selenide.impl;

import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
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

  @CheckReturnValue
  @Nonnull
  public List<DownloadedFile> files() {
    return files;
  }

  @CheckReturnValue
  @Nonnull
  public List<DownloadedFile> files(FileFilter fileFilter) {
    return files.stream().filter(fileFilter::match).collect(toList());
  }

  @CheckReturnValue
  @Nonnull
  public Optional<DownloadedFile> firstMatchingFile(FileFilter fileFilter) {
    return files.stream().filter(fileFilter::match).sorted(new DownloadDetector()).findFirst();
  }

  @CheckReturnValue
  @Nonnull
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

  @CheckReturnValue
  @Nonnull
  public File firstDownloadedFile(long timeout, FileFilter fileFilter) throws FileNotFoundException {
    return firstMatchingFile(fileFilter)
      .orElseThrow(() -> new FileNotFoundException(String.format("Failed to download file%s in %d ms.",
          fileFilter.description(), timeout).trim()
        )
      ).getFile();
  }
}
