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
import java.util.function.Predicate;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolder.class);

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

    waitUntilDownloadsCompleted(driver.browser(), timeout, pollingInterval, folder);
    Downloads newDownloads = waitForNewFiles(timeout, fileFilter, config, folder, downloadStartedAt);
    File downloadedFile = newDownloads.firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter);

    if (log.isDebugEnabled()) {
      log.debug("All downloaded files in {}: {}", folder, folder.files().stream().map(f -> f.getName()).collect(joining("\n")));
    }
    return archiveFile(config, downloadedFile);
  }

  private void waitUntilDownloadsCompleted(Browser browser, long timeout, long pollingInterval, DownloadsFolder folder) {
    pause(pollingInterval);
    if (browser.isChrome() || browser.isEdge() || browser.isOpera()) {
      waitUntilFileDisappears(folder, "crdownload", timeout, pollingInterval);
    }
    else if (browser.isFirefox()) {
      waitUntilFileDisappears(folder, "part", timeout, pollingInterval);
    }
  }

  private void waitUntilFileDisappears(DownloadsFolder folder, String extension, long timeout, long pollingInterval) {
    for (long start = currentTimeMillis(); currentTimeMillis() - start < timeout && folder.hasFiles(extension); ) {
      log.info("Found {} files in {}, waiting...", extension, folder);
      pause(pollingInterval);
    }

    if (folder.hasFiles(extension)) {
      log.warn("Folder {} still contains files {}", folder, extension);
    }
  }

  @Nonnull
  private Downloads waitForNewFiles(long timeout, FileFilter fileFilter, Config config,
                                    DownloadsFolder folder, long clickMoment) {
    HasDownloads hasDownloads = new HasDownloads(fileFilter, clickMoment);
    waiter.wait(folder, hasDownloads, timeout, config.pollingInterval());

    if (log.isInfoEnabled()) {
      log.info(hasDownloads.downloads.filesAsString());
    }
    if (log.isDebugEnabled()) {
      log.debug("All downloaded files in {}: {}", folder, folder.files().stream().map(f -> f.getName()).collect(joining("\n")));
    }
    return hasDownloads.downloads;
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

  @ParametersAreNonnullByDefault
  private static class HasDownloads implements Predicate<DownloadsFolder> {
    private final FileFilter fileFilter;
    private final long downloadStartedAt;
    Downloads downloads;

    private HasDownloads(FileFilter fileFilter, long downloadStartedAt) {
      this.fileFilter = fileFilter;
      this.downloadStartedAt = downloadStartedAt;
    }

    @Override
    public boolean test(DownloadsFolder folder) {
      downloads = new Downloads(newFiles(folder));
      return !downloads.files(fileFilter).isEmpty();
    }

    private List<DownloadedFile> newFiles(DownloadsFolder folder) {
      return folder.files().stream()
        .filter(File::isFile)
        .filter(file -> isFileModifiedLaterThan(file, downloadStartedAt))
        .map(file -> new DownloadedFile(file, emptyMap()))
        .collect(toList());
    }
  }

  /**
   * Depending on OS, file modification time can have seconds precision, not milliseconds.
   * We have to ignore the difference in milliseconds.
   */
  static boolean isFileModifiedLaterThan(File file, long timestamp) {
    return file.lastModified() >= timestamp / 1000L * 1000L;
  }
}
