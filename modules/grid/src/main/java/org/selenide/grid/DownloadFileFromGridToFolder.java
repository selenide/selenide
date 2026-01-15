package org.selenide.grid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Downloader;
import org.jspecify.annotations.Nullable;

import java.io.File;

public class DownloadFileFromGridToFolder extends com.codeborne.selenide.impl.DownloadFileToFolder {
  private final Downloader downloader;

  public DownloadFileFromGridToFolder() {
    this(new Downloader());
  }

  DownloadFileFromGridToFolder(Downloader downloader) {
    this.downloader = downloader;
  }

  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.isLocalBrowser() ?
      super.getDownloadsFolder(driver) :
      new GridDownloadsFolder(driver);
  }

  @Override
  protected File archiveFile(Driver driver, File downloadedFile) {
    if (driver.isLocalBrowser()) {
      return super.archiveFile(driver, downloadedFile);
    }
    return GridDownloader.archiveFile(downloader, driver, downloadedFile);
  }
}
