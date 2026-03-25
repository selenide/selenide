package org.selenide.grid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Downloader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;

class GridDownloader {
  private static final Logger log = LoggerFactory.getLogger(GridDownloader.class);

  @Deprecated
  static File archiveFile(Downloader downloader, Driver driver, File downloadedFile) throws IOException {
    return archiveFile(downloader, driver.config(), driver.getWebDriver(), downloadedFile);
  }

  static File archiveFile(Downloader downloader, Config config, WebDriver driver, File downloadedFile) throws IOException {
    RemoteWebDriver webDriver = unwrapRemoteWebDriver(driver);
    File uniqueFolder = downloader.prepareTargetFolder(config);
    webDriver.downloadFile(downloadedFile.getName(), uniqueFolder.toPath());
    File localFile = new File(uniqueFolder, downloadedFile.getName());
    log.debug("Copied the downloaded file {} from Grid to {}", downloadedFile, localFile);
    return localFile;
  }
}
