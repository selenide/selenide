package org.selenide.moon;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;

import java.util.List;

import static com.codeborne.selenide.files.DownloadedFile.fileWithName;
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
  public List<DownloadedFile> files() {
    List<String> files = moonClient.downloads();
    return files.stream().map(name -> fileWithName(name)).collect(toList());
  }

  /**
   * @param modifiedAfterTs ignored
   * @return all downloaded files because Moon API doesn't provide info about file modification time :(
   */
  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files();
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
