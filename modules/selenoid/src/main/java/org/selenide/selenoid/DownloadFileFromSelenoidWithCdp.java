package org.selenide.selenoid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class DownloadFileFromSelenoidWithCdp extends DownloadFileWithCdp {
  @Nullable
  @Override
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.isLocalBrowser() ?
      super.getDownloadsFolder(driver) :
      new SelenoidDownloadsFolder(driver);
  }

  @Override
  protected File archiveFile(Config config, WebDriver driver, File downloadedFile) throws IOException {
    if (isLocalBrowser(config)) {
      return super.archiveFile(config, driver, downloadedFile);
    }
    return SelenoidDownloader.archiveFile(downloader, config, driver, downloadedFile);
  }
}
