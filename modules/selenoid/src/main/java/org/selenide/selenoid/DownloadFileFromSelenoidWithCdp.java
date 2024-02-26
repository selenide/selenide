package org.selenide.selenoid;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.DownloadFileWithCdp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class DownloadFileFromSelenoidWithCdp extends DownloadFileWithCdp {
  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.isLocalBrowser() ?
      super.getDownloadsFolder(driver) :
      new SelenoidDownloadsFolder(driver);
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
