package org.selenide.moon;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;

import java.io.File;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.selenide.moon.MoonClient.clientFor;

public class MoonDownloadsFolder implements DownloadsFolder {
  private final MoonClient moonClient;

  public MoonDownloadsFolder(Driver driver) {
    this.moonClient = clientFor(driver);
  }

  @Override
  public void cleanupBeforeDownload() {
    moonClient.deleteDownloadedFiles();
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Override
  public List<File> files() {
    List<String> files = moonClient.downloads();
    return files.stream().map(name -> new File(name)).collect(toList());
  }

  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files().stream()
      .map(file -> new DownloadedFile(file, emptyMap()))
      .collect(toList());
  }

  @Override
  public String toString() {
    return moonClient.toString();
  }

  @Override
  public String getPath() {
    return "/tmp/not/really/used";
  }
}
