package org.selenide.moon;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Downloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.selenide.moon.MoonClient.clientFor;

public class MoonDownloader {
  private static final Logger log = LoggerFactory.getLogger(MoonDownloader.class);

  static File archiveFile(Downloader downloader, Driver driver, File downloadedFile) {
    MoonClient moonClient = clientFor(driver);
    File uniqueFolder = downloader.prepareTargetFolder(driver.config());
    File localFile = moonClient.download(downloadedFile.getName(), uniqueFolder);
    log.debug("Copied the downloaded file {} from Moon to {}", downloadedFile, localFile);
    return localFile;
  }
}
