package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.DownloadOptions.ContentStrategy;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.DownloadOptions.ContentStrategy.FULL_CONTENT;
import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

public class DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolder.class);
  private static final Set<String> CHROMIUM_TEMPORARY_FILES = Set.of("crdownload", "tmp");
  private static final Set<String> FIREFOX_TEMPORARY_FILES = Set.of("part");
  private static final DurationFormat df = new DurationFormat();

  private final Downloader downloader;

  DownloadFileToFolder(Downloader downloader) {
    this.downloader = downloader;
  }

  public DownloadFileToFolder() {
    this(new Downloader());
  }

  public File download(WebElementSource link,
                       WebElement clickable, long timeout, long requestedIncrementTimeout, DownloadOptions options) {
    return download(link, clickable, timeout, requestedIncrementTimeout,
      options.getFilter(), options.getAction(), options.contentStrategy());
  }

  @Deprecated
  public File download(WebElementSource link,
                       WebElement clickable, long timeout, long incrementTimeout,
                       FileFilter fileFilter,
                       DownloadAction action) {
    return download(link, clickable, timeout, incrementTimeout, fileFilter, action, FULL_CONTENT);
  }

  File download(WebElementSource link,
                WebElement clickable, long timeout, long requestedIncrementTimeout,
                FileFilter fileFilter,
                DownloadAction action,
                ContentStrategy contentStrategy
  ) {
    long incrementTimeout = Math.max(requestedIncrementTimeout, 1000);
    Driver driver = link.driver();
    WebDriver webDriver = driver.getWebDriver();
    Config config = driver.config();
    long pollingInterval = Math.max(config.pollingInterval(), 50);
    DownloadsFolder folder = getDownloadsFolder(driver);
    long start = currentTimeMillis();

    if (folder == null) {
      throw new IllegalStateException("Downloads folder is not configured");
    }

    folder.cleanupBeforeDownload();
    List<DownloadedFile> previousFiles = folder.files(); // some `DownloadsFolder` implementations cannot remove files

    action.perform(driver, clickable);

    waitForNewFiles(driver, fileFilter, folder, previousFiles, timeout, incrementTimeout, pollingInterval);
    waitUntilDownloadsCompleted(driver, folder, fileFilter, timeout, incrementTimeout, pollingInterval);

    Downloads newDownloads = new Downloads(folder.filesExcept(previousFiles));
    if (log.isInfoEnabled()) {
      log.info("Downloaded files in {}: {}", folder, newDownloads.filesAsString());
    }
    if (log.isDebugEnabled()) {
      log.debug("All downloaded files: {}", folder.filesAsString());
    }

    File downloadedFile = newDownloads.firstDownloadedFile(timeout, fileFilter);

    return switch (contentStrategy) {
      case FULL_CONTENT -> downloader.copyFileWithTimeout(downloadedFile.getName(),
        () -> archiveFile(config, webDriver, downloadedFile),
        timeout - (currentTimeMillis() - start)
      );
      case EMPTY_CONTENT -> downloader.mockFileContent(config, downloadedFile.getName());
    };
  }

  @Nullable
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.browserDownloadsFolder();
  }

  void waitUntilDownloadsCompleted(Driver driver, DownloadsFolder folder, FileFilter filter,
                                   long timeout, long incrementTimeout, long pollingInterval) {
    Browser browser = driver.browser();
    if (browser.isChrome() || browser.isEdge()) {
      waitUntilFileDisappears(driver, folder, CHROMIUM_TEMPORARY_FILES, filter, timeout, incrementTimeout, pollingInterval);
    } else if (browser.isFirefox()) {
      waitUntilFileDisappears(driver, folder, FIREFOX_TEMPORARY_FILES, filter, timeout, incrementTimeout, pollingInterval);
    } else {
      waitWhileFilesAreBeingModified(driver, folder, timeout, pollingInterval);
    }
  }

  private void waitUntilFileDisappears(Driver driver, DownloadsFolder folder, Set<String> extension, FileFilter filter,
                                       long timeout, long incrementTimeout, long pollingInterval) {
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
                                     "Apparently, the downloading hasn't completed in time.", folder, extension, df.format(timeout));
      throw new FileNotDownloadedError(message, timeout);
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
      } else {
        log.debug("Files has not been modified in last {} ms: {}", pollingInterval, times);
        if (currentTimeMillis() - lastModifiedAt > 1000) {
          log.debug("Files has not been modified during last {} ms.", currentTimeMillis() - lastModifiedAt);
          return;
        }
      }
    }
    log.warn("Files are still being modified during last {} ms.", currentTimeMillis() - lastModifiedAt);
  }

  void waitForNewFiles(Driver driver, FileFilter fileFilter, DownloadsFolder folder,
                       List<DownloadedFile> previousFiles,
                       long timeout, long incrementTimeout, long pollingInterval) {
    if (log.isDebugEnabled()) {
      log.debug("Waiting for files in {}...", folder);
    }

    long start = currentTimeMillis();
    for (; currentTimeMillis() - start <= timeout; pause(pollingInterval)) {
      Downloads downloads = new Downloads(folder.filesExcept(previousFiles));
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
      currentTimeMillis() - start, df.format(timeout));
  }

  protected void failFastIfNoChanges(Driver driver, DownloadsFolder folder, FileFilter filter,
                                     long start, long timeout, long incrementTimeout) {
    long lastFileUpdate = folder.lastModificationTime().orElse(start);
    long now = currentTimeMillis();
    long filesHasNotBeenUpdatedForMs = filesHasNotBeenUpdatedForMs(start, now, lastFileUpdate);
    if (filesHasNotBeenUpdatedForMs > incrementTimeout) {
      String message = String.format(
        "Failed to download file%s in %s: files in %s haven't been modified for %s " +
        "(started at: %s, lastFileUpdate: %s, now: %s, incrementTimeout: %s)" +
        "%nModification times: %s",
        filter.description(), df.format(timeout), folder, df.format(filesHasNotBeenUpdatedForMs),
        start, lastFileUpdate, now, df.format(incrementTimeout),
        folder.modificationTimes());
      throw new FileNotDownloadedError(message, timeout);
    }
  }

  long filesHasNotBeenUpdatedForMs(long downloadStartedAt, long now, long lastFileUpdate) {
    return now - Math.max(lastFileUpdate, downloadStartedAt);
  }

  private void pause(long milliseconds) {
    try {
      sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  protected boolean isLocalBrowser(Config config) {
    return config.remote() == null;
  }

  protected File archiveFile(Config config, WebDriver driver, File downloadedFile) throws IOException {
    File uniqueFolder = downloader.prepareTargetFolder(config);
    File archivedFile = new File(uniqueFolder, downloadedFile.getName());
    moveFile(downloadedFile, archivedFile);
    log.debug("Moved the downloaded file {} to {}", downloadedFile, archivedFile);
    return archivedFile;
  }
}
