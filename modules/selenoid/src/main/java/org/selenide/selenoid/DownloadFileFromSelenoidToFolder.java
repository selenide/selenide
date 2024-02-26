package org.selenide.selenoid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.Downloader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class DownloadFileFromSelenoidToFolder extends com.codeborne.selenide.impl.DownloadFileToFolder {
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
    else {
      // In Selenoid, we don't know files' modification time :(
    }
  }

  @Override
  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) {
    if (driver.isLocalBrowser()) {
      super.failFastIfNoChanges(driver, folder, filter, start, timeout, incrementTimeout);
    }
    else {
      // In Selenoid, we don't know files' modification time :(
    }
  }

  @Nonnull
  @Override
  protected File archiveFile(Driver driver, File downloadedFile) {
    if (driver.isLocalBrowser()) {
      return super.archiveFile(driver, downloadedFile);
    }
    return SelenoidDownloader.archiveFile(downloader, driver, downloadedFile);
  }
}
