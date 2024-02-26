package org.selenide.grid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.unwrapRemoteWebDriver;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

public class GridDownloadsFolder implements DownloadsFolder {
  private final RemoteWebDriver webDriver;
  private final Config config;

  public GridDownloadsFolder(Driver driver) {
    config = driver.config();
    webDriver = unwrapRemoteWebDriver(driver.getWebDriver());
  }

  @Override
  public void cleanupBeforeDownload() {
    webDriver.deleteDownloadableFiles();
  }

  @Override
  public void deleteIfEmpty() {
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<File> files() {
    return webDriver.getDownloadableFiles().stream().map(name -> new File(name)).collect(Collectors.toList());
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files().stream()
      .map(file -> new DownloadedFile(file, emptyMap()))
      .collect(toList());
  }

  @Nonnull
  @Override
  public String getPath() {
    return config.downloadsFolder();
  }

  @Override
  public String toString() {
    return "GridDownloadsFolder{" + getPath() + "}";
  }
}
