package org.selenide.grid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.Downloader;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;

@ParametersAreNonnullByDefault
public class DownloadFileInGrid extends com.codeborne.selenide.impl.DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileInGrid.class);
  private final Downloader downloader;

  public DownloadFileInGrid() {
    this(new Downloader());
  }

  DownloadFileInGrid(Downloader downloader) {
    this.downloader = downloader;
  }

  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return isLocalBrowser(driver) ?
      super.getDownloadsFolder(driver) :
      new GridDownloadsFolder(driver);
  }

  @Override
  protected void waitWhileFilesAreBeingModified(Driver driver, DownloadsFolder folder, long timeout, long pollingInterval) {
    if (isLocalBrowser(driver)) {
      super.waitWhileFilesAreBeingModified(driver, folder, timeout, pollingInterval);
    }
    else {
      // In Selenium Grid, we don't know files' modification time :(
    }
  }

  @Override
  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) {
    if (isLocalBrowser(driver)) {
      super.failFastIfNoChanges(driver, folder, filter, start, timeout, incrementTimeout);
    }
    else {
      // In Selenium Grid, we don't know files' modification time :(
    }
  }

  @Nonnull
  @Override
  protected File archiveFile(Driver driver, File downloadedFile) {
    if (isLocalBrowser(driver)) {
      return super.archiveFile(driver, downloadedFile);
    }
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

  private boolean isLocalBrowser(Driver driver) {
    return driver.config().remote() == null;
  }
}
