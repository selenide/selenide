package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolder.class);
  private static final String CHROME_TEMPORARY_FILE = "crdownload";
  private static final String FIREFOX_TEMPORARY_FILE = "part";

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
    DownloadsFolder folder = driver.browserDownloadsFolder();

    if (folder == null) {
      throw new IllegalStateException("Downloads folder is not configured");
    }

    folder.cleanupBeforeDownload();
    long downloadStartedAt = currentTimeMillis();

    action.perform(driver, clickable);

    waitForNewFiles(fileFilter, folder, downloadStartedAt, timeout, incrementTimeout, pollingInterval);
    waitUntilDownloadsCompleted(driver.browser(), folder, fileFilter, timeout, incrementTimeout, pollingInterval);

    Downloads newDownloads = new Downloads(newFiles(folder, downloadStartedAt));
    if (log.isInfoEnabled()) {
      log.info("Downloaded {}", newDownloads.filesAsString());
    }
    if (log.isDebugEnabled()) {
      log.debug("All downloaded files in {}: {}", folder, folder.files().stream().map(f -> f.getName()).collect(joining("\n")));
    }

    File downloadedFile = newDownloads.firstDownloadedFile(timeout, fileFilter);
    return archiveFile(config, downloadedFile);
  }

  private void waitUntilDownloadsCompleted(Browser browser, DownloadsFolder folder, FileFilter filter,
                                           long timeout, long incrementTimeout, long pollingInterval) throws FileNotFoundException {
    if (browser.isChrome() || browser.isEdge()) {
      waitUntilFileDisappears(folder, CHROME_TEMPORARY_FILE, filter, timeout, incrementTimeout, pollingInterval);
    }
    else if (browser.isFirefox()) {
      waitUntilFileDisappears(folder, FIREFOX_TEMPORARY_FILE, filter, timeout, incrementTimeout, pollingInterval);
    }
    else {
      waitWhileFilesAreBeingModified(folder, timeout, pollingInterval);
    }
  }

  private void waitUntilFileDisappears(DownloadsFolder folder, String extension, FileFilter filter,
                                       long timeout, long incrementTimeout, long pollingInterval) throws FileNotFoundException {
    for (long start = currentTimeMillis(); currentTimeMillis() - start <= timeout; pause(pollingInterval)) {
      if (!folder.hasFiles(extension, filter)) {
        log.debug("No {} files found in {}, conclude download is completed", extension, folder);
        return;
      }
      log.debug("Found {} files in {}, waiting for {} ms...", extension, folder, pollingInterval);
      failFastIfNoChanges(folder, filter, start, timeout, incrementTimeout);
    }

    if (folder.hasFiles(extension, filter)) {
      String message = String.format("Folder %s still contains files %s after %s ms. " +
        "Apparently, the downloading hasn't completed in time.", folder, extension, timeout);
      throw new FileNotFoundException(message);
    }
  }

  private void waitWhileFilesAreBeingModified(DownloadsFolder folder, long timeout, long pollingInterval) {
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

  private void waitForNewFiles(FileFilter fileFilter, DownloadsFolder folder, long clickMoment,
                               long timeout, long incrementTimeout, long pollingInterval) throws FileNotFoundException {
    for (long start = currentTimeMillis(); currentTimeMillis() - start <= timeout; pause(pollingInterval)) {
      Downloads downloads = new Downloads(newFiles(folder, clickMoment));
      List<DownloadedFile> matchingFiles = downloads.files(fileFilter);
      if (!matchingFiles.isEmpty()) {
        break;
      }
      log.debug("Matching files not found in {} (exists: {}): {}, all new files: {}, all files: {}", folder, folder.toFile().exists(),
        matchingFiles, downloads.filesAsString(), folder.files());
      failFastIfNoChanges(folder, fileFilter, start, timeout, incrementTimeout);
    }
  }

  private void failFastIfNoChanges(DownloadsFolder folder, FileFilter filter,
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
  private File archiveFile(Config config, File downloadedFile) {
    File uniqueFolder = downloader.prepareTargetFolder(config);
    File archivedFile = new File(uniqueFolder, downloadedFile.getName());
    moveFile(downloadedFile, archivedFile);
    return archivedFile;
  }

  private static List<DownloadedFile> newFiles(DownloadsFolder folder, long modifiedAfterTs) {
    return folder.files().stream()
      .filter(File::isFile)
      .filter(file -> isFileModifiedLaterThan(file, modifiedAfterTs))
      .map(file -> new DownloadedFile(file, emptyMap()))
      .collect(toList());
  }

  /**
   * Depending on OS, file modification time can have seconds precision, not milliseconds.
   * We have to ignore the difference in milliseconds.
   */
  static boolean isFileModifiedLaterThan(File file, long timestamp) {
    return file.lastModified() - timestamp >= -1000L;
  }
}
