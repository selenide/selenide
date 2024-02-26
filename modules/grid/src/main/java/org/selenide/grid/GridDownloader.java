package org.selenide.grid;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.impl.Downloader;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;

class GridDownloader {
  private static final Logger log = LoggerFactory.getLogger(GridDownloader.class);

  @Nonnull
  static File archiveFile(Downloader downloader, Driver driver, File downloadedFile) {
    RemoteWebDriver webDriver = unwrapRemoteWebDriver(driver.getWebDriver());
    File uniqueFolder = downloader.prepareTargetFolder(driver.config());
    try {
      webDriver.downloadFile(downloadedFile.getName(), uniqueFolder.toPath());
      File localFile = new File(uniqueFolder, downloadedFile.getName());
      log.debug("Copied the downloaded file {} from Grid to {}", downloadedFile, localFile);
      return localFile;
    }
    catch (IOException e) {
      throw new FileNotDownloadedError(driver, "Failed to copy downloaded file from grid", driver.config().timeout(), e);
    }
  }

}
