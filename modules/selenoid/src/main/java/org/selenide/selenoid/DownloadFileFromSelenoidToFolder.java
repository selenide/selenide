package org.selenide.selenoid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.Downloader;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DownloadFileFromSelenoidToFolder extends DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileFromSelenoidToFolder.class);

  private final Downloader downloader;

  public DownloadFileFromSelenoidToFolder() {
    this(new Downloader());
  }

  DownloadFileFromSelenoidToFolder(Downloader downloader) {
    this.downloader = downloader;
  }

  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.isLocalBrowser() ?
      super.getDownloadsFolder(driver) :
      new SelenoidDownloadsFolder(driver);
  }

  @Override
  protected void waitWhileFilesAreBeingModified(Driver driver, DownloadsFolder folder, long timeout, long pollingInterval) {
    if (driver.isLocalBrowser()) {
      super.waitWhileFilesAreBeingModified(driver, folder, timeout, pollingInterval);
    }
    log.warn("Unable to fail fast if no changes in Selenoid, we don't know files' modification time");
  }

  @Override
  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) {
    if (driver.isLocalBrowser()) {
      super.failFastIfNoChanges(driver, folder, filter, start, timeout, incrementTimeout);
    }
    log.warn("Unable to fail fast if no changes in Selenoid, we don't know files' modification time");
  }

  @Override
  protected File archiveFile(Config config, WebDriver driver, File downloadedFile) throws IOException {
    if (isLocalBrowser(config)) {
      return super.archiveFile(config, driver, downloadedFile);
    }
    return SelenoidDownloader.archiveFile(downloader, config, driver, downloadedFile);
  }
}
