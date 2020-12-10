package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public abstract class DownloadsFolder {
  protected final File folder;

  protected DownloadsFolder(File folder) {
    this.folder = folder;
  }

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

  public File file(String fileName) {
    return new File(folder, fileName).getAbsoluteFile();
  }

  @Override
  public String toString() {
    return folder.getAbsolutePath();
  }
}
