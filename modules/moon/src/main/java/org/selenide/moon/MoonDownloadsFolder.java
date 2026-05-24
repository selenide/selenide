package org.selenide.moon;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.files.DownloadedFile.fileWithName;
import static java.util.stream.Collectors.toList;
import static org.selenide.moon.MoonClient.clientFor;

public class MoonDownloadsFolder implements DownloadsFolder {
  private static final Logger log = LoggerFactory.getLogger(MoonDownloadsFolder.class);
  private final MoonClient moonClient;

  public MoonDownloadsFolder(Driver driver) {
    this.moonClient = clientFor(driver);
  }

  @Override
  public void cleanupBeforeDownload() {
    if (log.isDebugEnabled()) {
      log.debug("Going to clean Moon folder {} - found files: {}", moonClient.getSessionId(), moonClient.downloads());
    }

    moonClient.deleteDownloadedFiles();

    if (log.isDebugEnabled()) {
      log.debug("After clean folder {}: {}", moonClient.getSessionId(), moonClient.downloads());
    }
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Override
  public List<DownloadedFile> files() {
    List<String> files = moonClient.downloads();
    return files.stream().map(name -> fileWithName(name)).collect(toList());
  }

  @Override
  public String toString() {
    return "%s{%s}".formatted(getClass().getSimpleName(), moonClient.getSessionId());
  }

  @Override
  public String getPath() {
    return "/tmp/not/really/used";
  }
}
