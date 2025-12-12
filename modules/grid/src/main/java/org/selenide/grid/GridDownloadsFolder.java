package org.selenide.grid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;
import org.apache.commons.lang3.Strings;
import org.openqa.selenium.HasDownloads;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

public class GridDownloadsFolder implements DownloadsFolder {
  private final RemoteWebDriver webDriver;
  private final Config config;

  public GridDownloadsFolder(Driver driver) {
    config = driver.config();
    webDriver = unwrapRemoteWebDriver(driver.getWebDriver());
  }

  @Override
  public void cleanupBeforeDownload() {
    webDriver.deleteDownloadableFiles();
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Override
  public List<DownloadedFile> files() {
    return getDownloadedFiles().stream()
      .map(fileOnGrid -> new DownloadedFile(
        new File(fileOnGrid.getName()), fileOnGrid.getLastModifiedTime(), fileOnGrid.getSize(), emptyMap()))
      .toList();
  }

  /**
   * Temporary hack to work-around bug in Selenium
   */
  private List<HasDownloads.DownloadedFile> getDownloadedFiles() {
    for (int i = 0; i < 10; i++) {
      try {
        return webDriver.getDownloadedFiles();
      }
      catch (WebDriverException e) {
        if (Strings.CS.contains(e.getMessage(), "Failed to get file attributes")) continue;
        throw e;
      }
    }
    return webDriver.getDownloadedFiles();
  }

  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files().stream()
      .filter(fileInfo -> fileInfo.isFileModifiedLaterThan(modifiedAfterTs))
      .collect(toList());
  }

  @Override
  public String getPath() {
    return config.downloadsFolder();
  }

  @Override
  public String toString() {
    return "GridDownloadsFolder{" + getPath() + "}";
  }
}
