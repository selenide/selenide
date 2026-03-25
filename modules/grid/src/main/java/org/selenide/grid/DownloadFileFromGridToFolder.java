package org.selenide.grid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Downloader;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

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
  protected File archiveFile(Config config, WebDriver driver, File downloadedFile) throws IOException {
    if (isLocalBrowser(config)) {
      return super.archiveFile(config, driver, downloadedFile);
    }
    return GridDownloader.archiveFile(downloader, config, driver, downloadedFile);
  }
}
