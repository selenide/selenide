package org.selenide.selenoid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.Downloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;


@ParametersAreNonnullByDefault
public class DownloadFileInSelenoid extends com.codeborne.selenide.impl.DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileInSelenoid.class);
  private final Downloader downloader;

  public DownloadFileInSelenoid() {
    this(new Downloader());
  }

  DownloadFileInSelenoid(Downloader downloader) {
    this.downloader = downloader;
  }

  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return isLocalBrowser(driver) ?
      super.getDownloadsFolder(driver) :
      new SelenoidDownloadsFolder(driver);
  }

  @Override
  protected void waitWhileFilesAreBeingModified(Driver driver, DownloadsFolder folder, long timeout, long pollingInterval) {
    if (isLocalBrowser(driver)) {
      super.waitWhileFilesAreBeingModified(driver, folder, timeout, pollingInterval);
    }
    else {
      // In Selenoid, we don't know files' modification time :(
    }
  }

  @Override
  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) throws FileNotFoundException {
    if (isLocalBrowser(driver)) {
      super.failFastIfNoChanges(driver, folder, filter, start, timeout, incrementTimeout);
    }
    else {
      // In Selenoid, we don't know files' modification time :(
    }
  }

  @Nonnull
  @Override
  protected File archiveFile(Driver driver, File downloadedFile) {
    if (isLocalBrowser(driver)) {
      return super.archiveFile(driver, downloadedFile);
    }
    SelenoidClient selenoidClient = new SelenoidClient(driver.config().remote(), driver.getSessionId().toString());
    File uniqueFolder = downloader.prepareTargetFolder(driver.config());
    File localFile = selenoidClient.download(downloadedFile.getName(), uniqueFolder);
    log.debug("Copied the downloaded file {} from Selenoid to {}", downloadedFile, localFile);
    return localFile;
  }

  private boolean isLocalBrowser(Driver driver) {
    return driver.config().remote() == null;
  }
}
