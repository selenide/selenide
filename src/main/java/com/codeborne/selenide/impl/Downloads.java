package com.codeborne.selenide.impl;

import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.proxy.DownloadedFile;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

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
    StringBuilder sb = new StringBuilder();
    sb.append("Downloaded ").append(files.size()).append(" files:\n");

    int i = 0;
    for (DownloadedFile file : files) {
      sb.append("  #").append(++i).append("  ").append(file.getFile().getAbsolutePath()).append("\n");
    }
    return sb.toString();
  }

  public int size() {
    return files.size();
  }

  @CheckReturnValue
  @Nonnull
  public File firstDownloadedFile(String context, long timeout, FileFilter fileFilter) throws FileNotFoundException {
    if (size() == 0) {
      throw new FileNotFoundException("Failed to download file " + context + " in " + timeout + " ms.");
    }

    return firstMatchingFile(fileFilter)
      .orElseThrow(() -> new FileNotFoundException(String.format("Failed to download file %s in %d ms.%s",
        context, timeout, fileFilter.description())
        )
      ).getFile();
  }
}
