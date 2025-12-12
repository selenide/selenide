package org.selenide.selenoid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;

import java.util.List;

import static com.codeborne.selenide.files.DownloadedFile.fileWithName;
import static java.util.stream.Collectors.toList;
import static org.selenide.selenoid.SelenoidClient.clientFor;

public class SelenoidDownloadsFolder implements DownloadsFolder {
  private final SelenoidClient selenoidClient;

  public SelenoidDownloadsFolder(Driver driver) {
    this.selenoidClient = clientFor(driver);
  }

  @Override
  public void cleanupBeforeDownload() {
    selenoidClient.deleteDownloadedFiles();
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Override
  public List<DownloadedFile> files() {
    List<String> files = selenoidClient.downloads();
    return files.stream().map(name -> fileWithName(name)).collect(toList());
  }

  /**
   * @param modifiedAfterTs ignored
   * @return all downloaded files because Selenoid API doesn't provide info about file modification time :(
   */
  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files();
  }

  @Override
  public String toString() {
    return selenoidClient.toString();
  }

  @Override
  public String getPath() {
    return "/tmp/not/really/used";
  }
}
