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
  private final Waiter waiter;
  private final WindowsCloser windowsCloser;

  DownloadFileToFolder(Downloader downloader, Waiter waiter, WindowsCloser windowsCloser) {
    this.downloader = downloader;
    this.waiter = waiter;
    this.windowsCloser = windowsCloser;
  }

  public DownloadFileToFolder() {
    this(new Downloader(), new Waiter(), new WindowsCloser());
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter,
                       DownloadAction action) throws FileNotFoundException {

    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    return windowsCloser.runAndCloseArisedWindows(webDriver, () ->
      clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter, action)
    );
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndWaitForNewFilesInDownloadsFolder(WebElementSource anyClickableElement, WebElement clickable,
                                                        long timeout,
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

    waitForNewFiles(timeout, fileFilter, config, folder, downloadStartedAt);
    waitUntilDownloadsCompleted(driver.browser(), folder, timeout, pollingInterval);
    Downloads newDownloads = new Downloads(newFiles(folder, downloadStartedAt));

    if (log.isInfoEnabled()) {
      log.info("Downloaded {}", newDownloads.filesAsString());
    }

    File downloadedFile = newDownloads.firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter);

    if (log.isDebugEnabled()) {
      log.debug("All downloaded files in {}: {}", folder, folder.files().stream().map(f -> f.getName()).collect(joining("\n")));
    }
    return archiveFile(config, downloadedFile);
  }

  private void waitUntilDownloadsCompleted(Browser browser, DownloadsFolder folder,
                                           long timeout, long pollingInterval) {
    if (browser.isChrome() || browser.isEdge()) {
      waitUntilFileDisappears(folder, CHROME_TEMPORARY_FILE, timeout, pollingInterval);
    }
    else if (browser.isFirefox()) {
      waitUntilFileDisappears(folder, FIREFOX_TEMPORARY_FILE, timeout, pollingInterval);
    }
    else {
      waitWhileFilesAreBeingModified(folder, timeout, pollingInterval);
    }
  }

  private void waitUntilFileDisappears(DownloadsFolder folder, String extension, long timeout, long pollingInterval) {
    waiter.wait(timeout, pollingInterval, () -> {
      log.debug("Found {} files in {}, waiting for {} ms...", extension, folder, pollingInterval);
      return !folder.hasFiles(extension);
    });

    if (folder.hasFiles(extension)) {
      log.warn("Folder {} still contains files {} after {} ms.", folder, extension, timeout);
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

  private void waitForNewFiles(long timeout, FileFilter fileFilter, Config config,
                                    DownloadsFolder folder, long clickMoment) {
    waiter.wait(timeout, config.pollingInterval(), () -> {
      Downloads downloads = new Downloads(newFiles(folder, clickMoment));
      return !downloads.files(fileFilter).isEmpty();
    });
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
    return file.lastModified() >= timestamp / 1000L * 1000L;
  }
}
