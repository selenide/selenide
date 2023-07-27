package org.selenide.selenoid;

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

public class SelenoidDownloadsFolder implements DownloadsFolder {
  private final SelenoidClient selenoidClient;

  public SelenoidDownloadsFolder(Driver driver) {
    this.selenoidClient = new SelenoidClient(driver.config().remote(), driver.getSessionId().toString());
  }

  @Override
  public void cleanupBeforeDownload() {
    selenoidClient.deleteDownloadedFiles();
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<File> files() {
    List<String> files = selenoidClient.downloads();
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
    return selenoidClient.toString();
  }
}
