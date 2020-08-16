package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.proxy.DownloadedFile;
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
import static java.util.Collections.emptyMap;
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
                       FileFilter fileFilter) throws FileNotFoundException {

    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    return windowsCloser.runAndCloseArisedWindows(webDriver, () ->
      clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter)
    );
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndWaitForNewFilesInDownloadsFolder(WebElementSource anyClickableElement, WebElement clickable,
                                                        long timeout,
                                                        FileFilter fileFilter) throws FileNotFoundException {
    Driver driver = anyClickableElement.driver();
    Config config = driver.config();
    DownloadsFolder folder = driver.browserDownloadsFolder();

    folder.cleanupBeforeDownload();
    long downloadStartedAt = System.currentTimeMillis();

    clickable.click();

    Downloads newDownloads = waitForNewFiles(timeout, fileFilter, config, folder, downloadStartedAt);
    File downloadedFile = newDownloads.firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter);
    return archiveFile(config, downloadedFile);
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
      log.debug("All downloaded files in {}: {}", folder, folder.files());
    }
    return hasDownloads.downloads;
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
