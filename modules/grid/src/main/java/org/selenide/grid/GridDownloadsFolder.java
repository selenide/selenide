package org.selenide.grid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

public class GridDownloadsFolder implements DownloadsFolder {
  private final GridClient gridClient;

  public GridDownloadsFolder(Driver driver) {
    this.gridClient = new GridClient(driver.config().remote(), driver.getSessionId().toString());
  }

  @Override
  public void cleanupBeforeDownload() {
    gridClient.deleteDownloadedFiles();
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<File> files() {
    List<String> files = gridClient.downloads();
    return files.stream().map(name -> new File(name)).collect(Collectors.toList());
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files().stream()
      .map(file -> new DownloadedFile(file, emptyMap()))
      .collect(toList());
  }

  @Override
  public String toString() {
    return gridClient.toString();
  }
}
