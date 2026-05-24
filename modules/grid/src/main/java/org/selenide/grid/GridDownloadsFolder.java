package org.selenide.grid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;
import static java.util.Collections.emptyMap;

public class GridDownloadsFolder implements DownloadsFolder {
  private static final Logger log = LoggerFactory.getLogger(GridDownloadsFolder.class);

  private final RemoteWebDriver webDriver;
  private final Config config;

  public GridDownloadsFolder(Driver driver) {
    config = driver.config();
    webDriver = unwrapRemoteWebDriver(driver.getWebDriver());
  }

  @Override
  public void cleanupBeforeDownload() {
    if (log.isDebugEnabled()) {
      log.debug("Going to clean Grid folder {} - found files: {}", webDriver.getSessionId(), webDriver.getDownloadedFiles());
    }

    webDriver.deleteDownloadableFiles();

    if (log.isDebugEnabled()) {
      log.debug("After cleaning folder {}: {}", webDriver.getSessionId(), webDriver.getDownloadedFiles());
    }
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Override
  public List<DownloadedFile> files() {
    return webDriver.getDownloadedFiles().stream()
      .map(fileOnGrid -> new DownloadedFile(
        new File(fileOnGrid.getName()), fileOnGrid.getLastModifiedTime(), fileOnGrid.getSize(), emptyMap()))
      .toList();
  }

  @Override
  public String getPath() {
    return config.downloadsFolder();
  }

  @Override
  public String toString() {
    return "%s{%s}".formatted(getClass().getSimpleName(), webDriver.getSessionId());
  }
}
