package org.selenide.moon;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Downloader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class MoonDownloader {
  private static final Logger log = LoggerFactory.getLogger(MoonDownloader.class);

  @Deprecated
  static File archiveFile(Downloader downloader, Driver driver, File downloadedFile) {
    return archiveFile(downloader, driver.config(), driver.getWebDriver(), downloadedFile);
  }

  static File archiveFile(Downloader downloader, Config config, WebDriver driver, File downloadedFile) {
    String hubUrl = requireNonNull(config.remote(), "Remote browser URL is not configured");
    MoonClient moonClient = new MoonClient(hubUrl, ((RemoteWebDriver) driver).getSessionId());
    File uniqueFolder = downloader.prepareTargetFolder(config);
    File localFile = moonClient.download(downloadedFile.getName(), uniqueFolder);
    log.debug("Copied the downloaded file {} from Moon to {}", downloadedFile, localFile);
    return localFile;
  }
}
