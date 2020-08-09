package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.codeborne.selenide.impl.FileHelper.cleanupFolder;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.io.FileUtils.moveFile;

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
                       FileFilter fileFilter) throws IOException {

    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    return windowsCloser.runAndCloseArisedWindows(webDriver, () ->
      clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter)
    );
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndWaitForNewFilesInDownloadsFolder(WebElementSource anyClickableElement, WebElement clickable,
                                                        long timeout,
                                                        FileFilter fileFilter) throws IOException {
    Driver driver = anyClickableElement.driver();
    Config config = driver.config();
    File folder = driver.browserDownloadsFolder();

    PreviousDownloadsCompleted previousFiles = new PreviousDownloadsCompleted();
    waiter.wait(folder, previousFiles, timeout, config.pollingInterval());

    if (driver.isDownloadsFolderUnique()) {
      cleanupFolder(driver.browserDownloadsFolder());
    }
    clickable.click();

    HasDownloads hasDownloads = new HasDownloads(fileFilter, previousFiles.previousFiles);
    waiter.wait(folder, hasDownloads, timeout, config.pollingInterval());

    if (log.isInfoEnabled()) {
      log.info(hasDownloads.downloads.filesAsString());
    }
    File downloadedFile = hasDownloads.downloads.firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter);
    File uniqueFolder = downloader.prepareTargetFolder(config);
    File movedFile = new File(uniqueFolder, downloadedFile.getName());
    moveFile(downloadedFile, movedFile);
    return movedFile;
  }

  @CheckReturnValue
  @Nonnull
  private static List<File> allDownloadedFiles(File folder) {
    File[] files = folder.listFiles();
    if (log.isDebugEnabled()) {
      log.debug("all downloaded files in {}: {}", folder.getAbsolutePath(), Arrays.toString(files));
    }
    return files == null ? emptyList() : asList(files);
  }

  @ParametersAreNonnullByDefault
  private static class HasDownloads implements Predicate<File> {
    private final FileFilter fileFilter;
    private final Downloads previousFiles;
    Downloads downloads;

    private HasDownloads(FileFilter fileFilter, List<File> previousFiles) {
      this.fileFilter = fileFilter;
      this.previousFiles = toDownloads(previousFiles);
    }

    @Override
    public boolean test(File folder) {
      Downloads files = toDownloads(allDownloadedFiles(folder));
      List<DownloadedFile> newFiles = diff(files, previousFiles);
      downloads = new Downloads(newFiles);
      return !downloads.files(fileFilter).isEmpty();
    }

    private List<DownloadedFile> diff(Downloads currentFiles, Downloads previousFiles) {
      List<DownloadedFile> newFiles = new ArrayList<>(currentFiles.files());
      newFiles.removeAll(previousFiles.files());
      return newFiles;
    }

    private Downloads toDownloads(List<File> newFiles) {
      Downloads downloads = new Downloads();
      for (File file : newFiles) {
        if (file.exists()) {
          downloads.add(new DownloadedFile(file, emptyMap()));
        }
      }
      return downloads;
    }
  }

  @ParametersAreNonnullByDefault
  private static class PreviousDownloadsCompleted implements Predicate<File> {
    List<File> previousFiles = emptyList();

    @Override
    public boolean test(File folder) {
      List<File> files = allDownloadedFiles(folder);

      try {
        return previousFiles.size() == files.size();
      }
      finally {
        previousFiles = files;
      }
    }
  }
}
