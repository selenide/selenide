package com.codeborne.selenide;

import com.codeborne.selenide.files.FileFilter;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
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

  public abstract void cleanupBeforeDownload();

  public abstract void deleteIfEmpty();

  @CheckReturnValue
  @Nonnull
  public File file(String fileName) {
    return new File(folder, fileName).getAbsoluteFile();
  }

  public boolean hasFiles(String extension, FileFilter excludingFilter) {
    return files().stream()
      .anyMatch(file -> getExtension(file.getName()).equalsIgnoreCase(extension) && excludingFilter.notMatch(file));
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
}
