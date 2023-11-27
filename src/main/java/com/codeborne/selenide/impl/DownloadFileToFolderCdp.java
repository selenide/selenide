package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v119.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.openqa.selenium.devtools.v119.browser.model.DownloadProgress.State.COMPLETED;

@ParametersAreNonnullByDefault
public class DownloadFileToFolderCdp {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolderCdp.class);
  private final Downloader downloader;
  private AtomicBoolean downloadComplete = new AtomicBoolean(false);
  private AtomicReference<String> fileName = new AtomicReference<>();

  private DevTools devTools;

  DownloadFileToFolderCdp(Downloader downloader) {
    this.downloader = downloader;
  }

  public DownloadFileToFolderCdp() {
    this(new Downloader());
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter,
                       DownloadAction action) {

    return clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter, action);
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndWaitForNewFilesInDownloadsFolder(WebElementSource anyClickableElement, WebElement clickable,
                                                        long timeout,
                                                        FileFilter fileFilter,
                                                        DownloadAction action) {
    Driver driver = anyClickableElement.driver();
    Config config = driver.config();
    long pollingInterval = Math.max(config.pollingInterval(), 50);
    DownloadsFolder folder = getDownloadsFolder(driver);

    if (folder == null) {
      throw new IllegalStateException("Downloads folder is not configured");
    }

    folder.cleanupBeforeDownload();
    prepareDownloadWithCdp(driver);

    long downloadStartedAt = currentTimeMillis();

    action.perform(driver, clickable);

    waitUntilDownloadsCompleted(timeout, pollingInterval);

    Downloads newDownloads = new Downloads(folder.filesNewerThan(downloadStartedAt));
    if (log.isInfoEnabled()) {
      log.info("Downloaded files in {}: {}", folder, newDownloads.filesAsString());
    }
    if (log.isDebugEnabled()) {
      log.debug("All downloaded files: {}", folder.filesAsString());
    }

    File downloadedFile = newDownloads.firstDownloadedFile(driver, timeout, fileFilter);
    return archiveFile(driver, downloadedFile);
  }

  @Nullable
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.browserDownloadsFolder();
  }

  private void waitUntilDownloadsCompleted(long timeout, long pollingInterval) {
    for (long start = currentTimeMillis(); currentTimeMillis() - start < timeout; pause(pollingInterval)) {
      if (downloadComplete.get()) {
        log.info("File {} download is complete", fileName.get());
        return;
      }
    }
  }

  private void pause(long milliseconds) {
    try {
      sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  protected File archiveFile(Driver driver, File downloadedFile) {
    File uniqueFolder = downloader.prepareTargetFolder(driver.config());
    File archivedFile = new File(uniqueFolder, downloadedFile.getName());
    moveFile(downloadedFile, archivedFile);
    log.debug("Moved the downloaded file {} to {}", downloadedFile, archivedFile);
    return archivedFile;
  }

  private void initDevTools(Driver driver) {
    if (driver.browser().isChromium()) {
      devTools = ((HasDevTools) driver.getWebDriver()).getDevTools();
      devTools.createSession();
      devTools.send(Page.enable());
    } else {
        throw new RuntimeException("The browser that you choose doesn't have such functionality.");
    }
  }

  private void prepareDownloadWithCdp(Driver driver) {
    initDevTools(driver);

    devTools.send(org.openqa.selenium.devtools.v119.browser.Browser.setDownloadBehavior(
      org.openqa.selenium.devtools.v119.browser.Browser.SetDownloadBehaviorBehavior.ALLOW,
      Optional.empty(),
      Optional.of(getDownloadsFolder(driver).toString()),
      Optional.of(true)));

    devTools.addListener(org.openqa.selenium.devtools.v119.browser.Browser.downloadWillBegin(), handler -> {
      fileName.set(handler.getSuggestedFilename());
    });

    devTools.addListener(
      org.openqa.selenium.devtools.v119.browser.Browser.downloadProgress(),
      e -> {
        downloadComplete.set(e.getState() == COMPLETED);
        log.info("Download is in progress");
      });
  }
}
