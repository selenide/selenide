package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import com.google.common.collect.ImmutableSet;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

@ParametersAreNonnullByDefault
public class DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolder.class);
  private static final Set<String> CHROMIUM_TEMPORARY_FILES = ImmutableSet.of("crdownload", "tmp");
  private static final Set<String> FIREFOX_TEMPORARY_FILES = ImmutableSet.of("part");

  private final Downloader downloader;
  private final WindowsCloser windowsCloser;

  DownloadFileToFolder(Downloader downloader, WindowsCloser windowsCloser) {
    this.downloader = downloader;
    this.windowsCloser = windowsCloser;
  }

  public DownloadFileToFolder() {
    this(new Downloader(), new WindowsCloser());
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout, long incrementTimeout,
                       FileFilter fileFilter,
                       DownloadAction action) throws FileNotFoundException {

    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    long minimalIncrementTimeout = Math.max(incrementTimeout, 1000);
    return windowsCloser.runAndCloseArisedWindows(webDriver, () ->
      clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, minimalIncrementTimeout, fileFilter, action)
    );
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndWaitForNewFilesInDownloadsFolder(WebElementSource anyClickableElement, WebElement clickable,
                                                        long timeout, long incrementTimeout,
                                                        FileFilter fileFilter,
                                                        DownloadAction action) throws FileNotFoundException {
    Driver driver = anyClickableElement.driver();
    Config config = driver.config();
    long pollingInterval = Math.max(config.pollingInterval(), 50);
    DownloadsFolder folder = getDownloadsFolder(driver);

    if (folder == null) {
      throw new IllegalStateException("Downloads folder is not configured");
    }

    folder.cleanupBeforeDownload();
    long downloadStartedAt = currentTimeMillis();

    action.perform(driver, clickable);

    waitForNewFiles(driver, fileFilter, folder, downloadStartedAt, timeout, incrementTimeout, pollingInterval);
    waitUntilDownloadsCompleted(driver, folder, fileFilter, timeout, incrementTimeout, pollingInterval);

    Downloads newDownloads = new Downloads(folder.filesNewerThan(downloadStartedAt));
    if (log.isInfoEnabled()) {
      log.info("Downloaded files in {}: {}", folder, newDownloads.filesAsString());
    }
    if (log.isDebugEnabled()) {
      log.debug("All downloaded files: {}", folder.filesAsString());
    }

    File downloadedFile = newDownloads.firstDownloadedFile(timeout, fileFilter);
    return archiveFile(driver, downloadedFile);
  }

  @Nullable
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.browserDownloadsFolder();
  }

  private void waitUntilDownloadsCompleted(Driver driver, DownloadsFolder folder, FileFilter filter,
                                           long timeout, long incrementTimeout, long pollingInterval) throws FileNotFoundException {
    Browser browser = driver.browser();
    if (browser.isChrome() || browser.isEdge()) {
      waitUntilFileDisappears(driver, folder, CHROMIUM_TEMPORARY_FILES, filter, timeout, incrementTimeout, pollingInterval);
    }
    else if (browser.isFirefox()) {
      waitUntilFileDisappears(driver, folder, FIREFOX_TEMPORARY_FILES, filter, timeout, incrementTimeout, pollingInterval);
    }
    else {
      waitWhileFilesAreBeingModified(driver, folder, timeout, pollingInterval);
    }
  }

  private void waitUntilFileDisappears(Driver driver, DownloadsFolder folder, Set<String> extension, FileFilter filter,
                                       long timeout, long incrementTimeout, long pollingInterval) throws FileNotFoundException {
    for (long start = currentTimeMillis(); currentTimeMillis() - start <= timeout; pause(pollingInterval)) {
      if (!folder.hasFiles(extension, filter)) {
        log.debug("No {} files found, conclude download is completed (filter: {})", extension, filter);
        return;
      }
      log.debug("Found {} files, waiting for {} ms (filter: {})...", extension, pollingInterval, filter);
      failFastIfNoChanges(driver, folder, filter, start, timeout, incrementTimeout);
    }

    if (folder.hasFiles(extension, filter)) {
      String message = String.format("Folder %s still contains files %s after %s ms. " +
                                     "Apparently, the downloading hasn't completed in time.", folder, extension, timeout);
      throw new FileNotFoundException(message);
    }
  }

  protected void waitWhileFilesAreBeingModified(Driver driver, DownloadsFolder folder, long timeout, long pollingInterval) {
    Map<String, Long> times = folder.modificationTimes();
    long lastModifiedAt = currentTimeMillis();

    for (long start = currentTimeMillis(); currentTimeMillis() - start < timeout; pause(pollingInterval)) {
      var newTimes = folder.modificationTimes();
      if (!newTimes.equals(times)) {
        log.debug("Files has been modified - old: {}, new: {}", times, newTimes);
        lastModifiedAt = currentTimeMillis();
        times = newTimes;
      }
      else {
        log.debug("Files has not been modified in last {} ms: {}", pollingInterval, times);
        if (currentTimeMillis() - lastModifiedAt > 1000) {
          log.debug("Files has not been modified during last {} ms.", currentTimeMillis() - lastModifiedAt);
          return;
        }
      }
    }
    log.warn("Files are still being modified during last {} ms.", currentTimeMillis() - lastModifiedAt);
  }

  private void waitForNewFiles(Driver driver, FileFilter fileFilter, DownloadsFolder folder, long clickMoment,
                               long timeout, long incrementTimeout, long pollingInterval) throws FileNotFoundException {
    if (log.isDebugEnabled()) {
      log.debug("Waiting for files in {}...", folder);
    }

    long start = currentTimeMillis();
    for (; currentTimeMillis() - start <= timeout; pause(pollingInterval)) {
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
    }

    log.debug("Matching files still not found -> stop waiting for new files after {} ms. (timeout: {} ms.)",
      currentTimeMillis() - start, timeout);
  }

  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) throws FileNotFoundException {
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
      throw new FileNotFoundException(message);
    }
  }

  long filesHasNotBeenUpdatedForMs(long downloadStartedAt, long now, long lastFileUpdate) {
    return now - Math.max(lastFileUpdate, downloadStartedAt);
  }

  private void pause(long milliseconds) {
    try {
      sleep(milliseconds);
    }
    catch (InterruptedException e) {
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
}
