package com.codeborne.selenide.impl;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.files.DownloadAction;
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
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.openqa.selenium.devtools.v119.browser.model.DownloadProgress.State.COMPLETED;

@ParametersAreNonnullByDefault
public class DownloadFileToFolderCdp extends DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolderCdp.class);
  private AtomicBoolean downloadComplete = new AtomicBoolean(false);
  private AtomicReference<String> fileName = new AtomicReference<>();

  private DevTools devTools;

  DownloadFileToFolderCdp(Downloader downloader) {
    super(downloader, new WindowsCloser());
  }

  public DownloadFileToFolderCdp() {
    this(new Downloader());
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout, long incrementTimeout,
                       FileFilter fileFilter,
                       DownloadAction action) {
    prepareDownloadWithCdp(anyClickableElement.driver());
    return clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable,
      timeout, incrementTimeout, fileFilter, action);
  }

  @Override
  void waitUntilDownloadsCompleted(Driver driver, DownloadsFolder folder, FileFilter filter,
                                   long timeout, long incrementTimeout, long pollingInterval) {
    waitUntilDownloadsCompleted(timeout, pollingInterval);
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

  private void initDevTools(Driver driver) {
    if (driver.getWebDriver() instanceof HasDevTools) {
      devTools = ((HasDevTools) driver.getWebDriver()).getDevTools();
      devTools.createSessionIfThereIsNotOne();
      devTools.send(Page.enable());
    } else {
      throw new IllegalArgumentException("The browser you selected \"%s\" doesn't have Chrome Devtools protocol functionality."
        .formatted(driver.browser().name));
    }
  }

  private void prepareDownloadWithCdp(Driver driver) {
    initDevTools(driver);

    if (driver.browser().isChromium()) {
      devTools.send(Browser.setDownloadBehavior(
        Browser.SetDownloadBehaviorBehavior.ALLOW,
        Optional.empty(),
        Optional.of(getDownloadsFolder(driver).toString()),
        Optional.of(true)));
    }

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
}
