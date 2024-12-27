package org.selenide.moon;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import org.jspecify.annotations.Nullable;

import java.io.File;

public class DownloadFileFromMoonWithCdp extends DownloadFileWithCdp {
  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.isLocalBrowser() ?
      super.getDownloadsFolder(driver) :
      new MoonDownloadsFolder(driver);
  }

  @Override
  protected File archiveFile(Driver driver, File downloadedFile) {
    if (driver.isLocalBrowser()) {
      return super.archiveFile(driver, downloadedFile);
    }
    return MoonDownloader.archiveFile(downloader, driver, downloadedFile);
  }
}
