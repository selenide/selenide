package org.selenide.moon;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.Downloader;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DownloadFileFromMoonToFolder extends DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileFromMoonToFolder.class);

  private final Downloader downloader;

  public DownloadFileFromMoonToFolder() {
    this(new Downloader());
  }

  DownloadFileFromMoonToFolder(Downloader downloader) {
    this.downloader = downloader;
  }

  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.isLocalBrowser() ?
      super.getDownloadsFolder(driver) :
      new MoonDownloadsFolder(driver);
  }

  @Override
  protected void waitWhileFilesAreBeingModified(Driver driver, DownloadsFolder folder, long timeout, long pollingInterval) {
    if (driver.isLocalBrowser()) {
      super.waitWhileFilesAreBeingModified(driver, folder, timeout, pollingInterval);
    }
    log.warn("Unable to fail fast if no changes in Moon, we don't know files' modification time");
  }

  @Override
  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) {
    if (driver.isLocalBrowser()) {
      super.failFastIfNoChanges(driver, folder, filter, start, timeout, incrementTimeout);
    }
    log.warn("Unable to fail fast if no changes in Moon, we don't know files' modification time");
  }

  @Override
  protected File archiveFile(Driver driver, File downloadedFile) {
    if (driver.isLocalBrowser()) {
      return super.archiveFile(driver, downloadedFile);
    }
    return MoonDownloader.archiveFile(downloader, driver, downloadedFile);
  }
}
