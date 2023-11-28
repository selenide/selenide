package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v119.browser.Browser;
import org.openqa.selenium.devtools.v119.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
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
                       WebElement clickable, long timeout, long incrementTimeout,
                       FileFilter fileFilter,
                       DownloadAction action) {

    return clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, incrementTimeout, fileFilter, action);
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndWaitForNewFilesInDownloadsFolder(WebElementSource anyClickableElement, WebElement clickable,
                                                        long timeout, long incrementTimeout,
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

    waitForNewFiles(driver, fileFilter, folder, downloadStartedAt, timeout, incrementTimeout, pollingInterval);
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
    Stopwatch stopwatch = new Stopwatch(timeout);
    do {
      if (downloadComplete.get()) {
        log.debug("File {} download is complete", fileName.get());
        return;
      }
      stopwatch.sleep(pollingInterval);
    } while (!stopwatch.isTimeoutReached());
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
      devTools.createSessionIfThereIsNotOne();
      devTools.send(Page.enable());
    } else {
      throw new RuntimeException("The browser you selected \"%s\" doesn't have Chrome Devtools protocol functionality."
        .formatted(driver.browser().name));
    }
  }

  private void prepareDownloadWithCdp(Driver driver) {
    initDevTools(driver);

    devTools.send(Browser.setDownloadBehavior(
      Browser.SetDownloadBehaviorBehavior.ALLOW,
      Optional.empty(),
      Optional.of(getDownloadsFolder(driver).toString()),
      Optional.of(true)));

    devTools.addListener(Browser.downloadWillBegin(), handler -> {
      fileName.set(handler.getSuggestedFilename());
    });

    devTools.addListener(
      Browser.downloadProgress(),
      e -> {
        downloadComplete.set(e.getState() == COMPLETED);
        log.debug("Download is in progress");
      });
  }

  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) {
    long lastFileUpdate = folder.lastModificationTime().orElse(-1L);
    long now = currentTimeMillis();
    long filesHasNotBeenUpdatedForMs = filesHasNotBeenUpdatedForMs(start, now, lastFileUpdate);
    if (filesHasNotBeenUpdatedForMs > incrementTimeout) {
      String message = String.format(
        "Failed to download file%s in %d ms: files in %s haven't been modified for %s ms. " +
        "(started at: %s, lastFileUpdate: %s, now: %s, incrementTimeout: %s)" +
        "%nModification times: %s",
        filter.description(), timeout, folder, filesHasNotBeenUpdatedForMs,
        start, lastFileUpdate, now, incrementTimeout,
        folder.modificationTimes());
      throw new FileNotDownloadedError(driver, message, timeout);
    }
  }

  long filesHasNotBeenUpdatedForMs(long downloadStartedAt, long now, long lastFileUpdate) {
    return now - Math.max(lastFileUpdate, downloadStartedAt);
  }

  private void waitForNewFiles(Driver driver, FileFilter fileFilter, DownloadsFolder folder, long clickMoment,
                               long timeout, long incrementTimeout, long pollingInterval) {
    if (log.isDebugEnabled()) {
      log.debug("Waiting for files in {}...", folder);
    }

    long start = currentTimeMillis();
    Stopwatch stopwatch = new Stopwatch(start);

    do {
      Downloads downloads = new Downloads(folder.filesNewerThan(clickMoment));
      List<DownloadedFile> matchingFiles = downloads.files(fileFilter);
      if (!matchingFiles.isEmpty()) {
        log.debug("Matching files found: {}, all new files: {}, all files: {}",
          matchingFiles, downloads.filesAsString(), folder.filesAsString());
        return;
      }
      log.debug("Matching files not found: {}, all new files: {}, all files: {}",
        matchingFiles, downloads.filesAsString(), folder.filesAsString());
      failFastIfNoChanges(driver, folder, fileFilter, start, timeout, incrementTimeout);
      stopwatch.sleep(pollingInterval);
    } while (!stopwatch.isTimeoutReached());

    log.debug("Matching files still not found -> stop waiting for new files after {} ms. (timeout: {} ms.)",
      currentTimeMillis() - start, timeout);
  }
}
