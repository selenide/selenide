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
public class DownloadFileToFolderCdp {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolderCdp.class);
  private final Downloader downloader = new Downloader();
  private final WindowsCloser windowsCloser = new WindowsCloser();

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout, long incrementTimeout,
                       FileFilter fileFilter,
                       DownloadAction action) {
    Driver driver = anyClickableElement.driver();
    DevTools devTools = initDevTools(driver);

    if (driver.browser().isChromium()) {
//      devTools.send(Browser.setDownloadBehavior(
//        Browser.SetDownloadBehaviorBehavior.ALLOW,
//        Optional.empty(),
//        Optional.of(getDownloadsFolder(driver).toString()),
//        Optional.of(true)));
    }

    final AtomicBoolean downloadComplete = new AtomicBoolean(false);
    final AtomicReference<String> fileName = new AtomicReference<>();

    devTools.addListener(Browser.downloadWillBegin(), handler -> {
      fileName.set(handler.getSuggestedFilename());
    });

    devTools.addListener(
      Browser.downloadProgress(),
      e -> {
        downloadComplete.set(e.getState() == COMPLETED);
        log.debug("Download is in progress");
      });
//    return clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable,
//      timeout, incrementTimeout, fileFilter, action);

    // folder.cleanupBeforeDownload();
    action.perform(driver, clickable);
  }

  @Override
  void waitUntilDownloadsCompleted(Driver driver, DownloadsFolder folder, FileFilter filter,
                                   long timeout, long incrementTimeout, long pollingInterval) {
    Stopwatch stopwatch = new Stopwatch(timeout);
    do {
      if (downloadComplete.get()) {
        log.debug("File {} download is complete", fileName.get());
        return;
      }
      stopwatch.sleep(pollingInterval);
    } while (!stopwatch.isTimeoutReached());
  }

  private DevTools initDevTools(Driver driver) {
    if (driver.getWebDriver() instanceof HasDevTools) {
      DevTools devTools = ((HasDevTools) driver.getWebDriver()).getDevTools();
      devTools.createSessionIfThereIsNotOne();
      devTools.send(Page.enable());
      return devTools;
    } else {
      throw new IllegalArgumentException("The browser you selected \"%s\" doesn't have Chrome Devtools protocol functionality."
        .formatted(driver.browser().name));
    }
  }
}
