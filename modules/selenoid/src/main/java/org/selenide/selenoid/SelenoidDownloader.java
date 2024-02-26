package org.selenide.selenoid;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Downloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;

import static org.selenide.selenoid.SelenoidClient.clientFor;

public class SelenoidDownloader {
  private static final Logger log = LoggerFactory.getLogger(SelenoidDownloader.class);

  @Nonnull
  static File archiveFile(Downloader downloader, Driver driver, File downloadedFile) {
    SelenoidClient selenoidClient = clientFor(driver);
    File uniqueFolder = downloader.prepareTargetFolder(driver.config());
    File localFile = selenoidClient.download(downloadedFile.getName(), uniqueFolder);
    log.debug("Copied the downloaded file {} from Selenoid to {}", downloadedFile, localFile);
    return localFile;
  }
}
