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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@ParametersAreNonnullByDefault
public class DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolder.class);

  private final Waiter waiter;
  private final WindowsCloser windowsCloser;

  DownloadFileToFolder(Waiter waiter, WindowsCloser windowsCloser) {
    this.waiter = waiter;
    this.windowsCloser = windowsCloser;
  }

  public DownloadFileToFolder() {
    this(new Waiter(), new WindowsCloser());
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

    PreviousDownloadsCompleted previousFiles = new PreviousDownloadsCompleted();
    waiter.wait(folder, previousFiles, timeout, config.pollingInterval());

    clickable.click();

    HasDownloads hasDownloads = new HasDownloads(fileFilter, previousFiles.previousFiles);
    waiter.wait(folder, hasDownloads, timeout, config.pollingInterval());

    if (log.isInfoEnabled()) {
      log.info(hasDownloads.downloads.filesAsString());
    }
    return hasDownloads.downloads.firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter);
  }

  @ParametersAreNonnullByDefault
  private static class HasDownloads implements Predicate<DownloadsFolder> {
    private final FileFilter fileFilter;
    private final Downloads previousFiles;
    Downloads downloads;

    private HasDownloads(FileFilter fileFilter, List<File> previousFiles) {
      this.fileFilter = fileFilter;
      this.previousFiles = toDownloads(previousFiles);
    }

    @Override
    public boolean test(DownloadsFolder folder) {
      Downloads files = toDownloads(folder.allDownloadedFiles());
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
  private static class PreviousDownloadsCompleted implements Predicate<DownloadsFolder> {
    List<File> previousFiles = emptyList();

    @Override
    public boolean test(DownloadsFolder folder) {
      List<File> files = folder.allDownloadedFiles();

      try {
        return previousFiles.size() == files.size();
      }
      finally {
        previousFiles = files;
      }
    }
  }
}
