package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v120.browser.Browser;
import org.openqa.selenium.devtools.v120.browser.model.DownloadProgress;
import org.openqa.selenium.devtools.v120.browser.model.DownloadWillBegin;
import org.openqa.selenium.devtools.v120.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.util.Collections.emptyMap;
import static org.openqa.selenium.devtools.v120.browser.Browser.downloadProgress;
import static org.openqa.selenium.devtools.v120.browser.Browser.downloadWillBegin;

@ParametersAreNonnullByDefault
public class DownloadFileToFolderCdp {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileToFolderCdp.class);
  private static final AtomicLong SEQUENCE = new AtomicLong();

  private final Downloader downloader;

  DownloadFileToFolderCdp(Downloader downloader) {
    this.downloader = downloader;
  }

  public DownloadFileToFolderCdp() {
    this(new Downloader());
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter,
                       DownloadAction action) {

    Driver driver = anyClickableElement.driver();
    DevTools devTools = initDevTools(driver);

    AtomicBoolean downloadComplete = new AtomicBoolean(false);
    AtomicReference<String> fileName = new AtomicReference<>();

    // Init download behaviour and listeners
    prepareDownloadWithCdp(driver, devTools, fileName, downloadComplete, timeout);

    // Perform action an element that begins download process
    action.perform(anyClickableElement.driver(), clickable);

    try {
      // Wait until download
      File file = waitUntilDownloadsCompleted(anyClickableElement.driver(), timeout, downloadComplete, fileName);

      //
      if (!fileFilter.match(new DownloadedFile(file, emptyMap()))) {
        String message = String.format("Failed to download file in %d ms.%s;%n actually downloaded: %s",
          timeout, fileFilter.description(), file.getAbsolutePath());
        throw new FileNotDownloadedError(driver, message, timeout);
      }

      // Move file to unique folder
      return archiveFile(anyClickableElement.driver(), file);
    }
    finally {
      devTools.clearListeners();
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

  private File waitUntilDownloadsCompleted(Driver driver, long timeout,
                                           AtomicBoolean downloadComplete, AtomicReference<String> fileName) {
    long pollingInterval = Math.max(driver.config().pollingInterval(), 100);
    Stopwatch stopwatch = new Stopwatch(timeout);
    do {
      if (downloadComplete.get()) {
        log.debug("File {} download is complete after {} ms.", fileName, stopwatch.getElapsedTimeMs());
        return new File(driver.browserDownloadsFolder().toString(), fileName.get());
      }
      stopwatch.sleep(pollingInterval);
    } while (!stopwatch.isTimeoutReached());

    String message = "Failed to download file in %d ms".formatted(timeout);
    throw new FileNotDownloadedError(driver, message, timeout);
  }

  private DevTools initDevTools(Driver driver) {
    DevTools devTools;
    if (driver.browser().isChromium()) {
      devTools = ((HasDevTools) driver.getWebDriver()).getDevTools();
      devTools.createSessionIfThereIsNotOne();
      devTools.send(Page.enable());
      return devTools;
    } else {
      throw new IllegalArgumentException("The browser you selected \"%s\" doesn't have Chrome Devtools protocol functionality."
        .formatted(driver.browser().name));
    }
  }

  private void prepareDownloadWithCdp(Driver driver, DevTools devTools,
                                      AtomicReference<String> fileName, AtomicBoolean downloadComplete, long timeout) {
    devTools.send(Browser.setDownloadBehavior(
      Browser.SetDownloadBehaviorBehavior.ALLOW,
      Optional.empty(),
      Optional.of(driver.browserDownloadsFolder().toString()),
      Optional.of(true)));

    devTools.clearListeners();
    devTools.addListener(downloadWillBegin(), new DownloadWillBeginListener(id(), fileName));
    devTools.addListener(downloadProgress(), new DownloadProgressListener(id(), driver, downloadComplete, timeout));
  }

  private static long id() {
    return SEQUENCE.incrementAndGet();
  }

  private record DownloadWillBeginListener(long id, AtomicReference<String> fileName) implements Consumer<DownloadWillBegin> {
    @Override
    public void accept(DownloadWillBegin e) {
      log.debug("[{}] Download will begin with suggested file name \"{}\" (url: \"{}\", frameId: {}, guid: {})",
        id, e.getSuggestedFilename(), e.getUrl(), e.getFrameId(), e.getGuid());
      fileName.set(e.getSuggestedFilename());
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "#" + id;
    }
  }

  private record DownloadProgressListener(long id, Driver driver, AtomicBoolean downloadComplete, long timeout)
    implements Consumer<DownloadProgress> {
    @Override
    public void accept(DownloadProgress e) {
      log.debug("[{}] Download is {} (received bytes: {}, total bytes: {}, guid: {})",
        id, e.getState(), e.getReceivedBytes(), e.getTotalBytes(), e.getGuid());

      switch (e.getState()) {
        case CANCELED -> {
          String message = "File download is %s (received bytes: %s, total bytes: %s, guid: %s)".formatted(
            e.getState(), e.getReceivedBytes(), e.getTotalBytes(), e.getGuid());
          throw new FileNotDownloadedError(driver, message, timeout);
        }
        case COMPLETED -> downloadComplete.set(true);
        case INPROGRESS -> {
        }
      }
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "#" + id;
    }
  }
}
