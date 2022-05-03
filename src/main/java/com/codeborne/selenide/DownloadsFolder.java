package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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

  @CheckReturnValue
  @Nonnull
  public File file(String fileName) {
    return new File(folder, fileName).getAbsoluteFile();
  }

  @Override
  public String toString() {
    return folder.getPath();
  }

  public boolean hasFiles(String extension) {
    return files().stream()
      .anyMatch(file -> getExtension(file.getName()).equalsIgnoreCase(extension));
  }
}
