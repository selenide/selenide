package org.selenide.selenoid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.files.DownloadedFile.fileWithName;
import static java.util.stream.Collectors.toList;
import static org.selenide.selenoid.SelenoidClient.clientFor;

public class SelenoidDownloadsFolder implements DownloadsFolder {
  private static final Logger log = LoggerFactory.getLogger(SelenoidDownloadsFolder.class);
  private final SelenoidClient selenoidClient;

  public SelenoidDownloadsFolder(Driver driver) {
    this.selenoidClient = clientFor(driver);
  }

  @Override
  public void cleanupBeforeDownload() {
    if (log.isDebugEnabled()) {
      log.debug("Going to clean Selenoid folder {} - found files: {}", selenoidClient.getSessionId(), selenoidClient.downloads());
    }

    selenoidClient.deleteDownloadedFiles();

    if (log.isDebugEnabled()) {
      log.debug("After clean folder {}: {}", selenoidClient.getSessionId(), selenoidClient.downloads());
    }
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Override
  public List<DownloadedFile> files() {
    List<String> files = selenoidClient.downloads();
    return files.stream().map(name -> fileWithName(name)).collect(toList());
  }

  @Override
  public String toString() {
    return "%s{%s}".formatted(getClass().getSimpleName(), selenoidClient.getSessionId());
  }

  @Override
  public String getPath() {
    return "/tmp/not/really/used";
  }
}
