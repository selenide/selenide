package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.DownloadOptions.ContentStrategy;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v148.browser.Browser;
import org.openqa.selenium.devtools.v148.browser.model.DownloadProgress;
import org.openqa.selenium.devtools.v148.browser.model.DownloadWillBegin;
import org.openqa.selenium.devtools.v148.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static org.openqa.selenium.devtools.v148.browser.Browser.downloadProgress;
import static org.openqa.selenium.devtools.v148.browser.Browser.downloadWillBegin;

public class DownloadFileWithCdp {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileWithCdp.class);
  private static final DurationFormat df = new DurationFormat();
  private static final AtomicLong SEQUENCE = new AtomicLong();

  protected final Downloader downloader;

  DownloadFileWithCdp(Downloader downloader) {
    this.downloader = downloader;
  }

  public DownloadFileWithCdp() {
    this(new Downloader());
  }

  @Nullable
  protected DownloadsFolder getDownloadsFolder(Driver driver) {
    return driver.browserDownloadsFolder();
  }

  public List<File> download(Driver driver,
                             WebElement clickable, long timeout, long incrementTimeout, DownloadOptions options) {
    FileFilter fileFilter = options.getFilter();
    DownloadAction action = options.getAction();
    ContentStrategy contentStrategy = options.contentStrategy();

    long start = currentTimeMillis();
    Config config = driver.config();
    WebDriver webDriver = driver.getWebDriver();
    DevTools devTools = initDevTools(driver);
    DownloadsFolder downloadsFolder = requireNonNull(getDownloadsFolder(driver), "Webdriver downloads folder is not configured");
    CdpDownloads downloads = new CdpDownloads(downloadsFolder, new ConcurrentHashMap<>(1));

    // Init download behaviour and listeners
    prepareDownloadWithCdp(driver, devTools, downloads, timeout);

    // Perform action an element that begins download process
    action.perform(driver, clickable);

    try {
      MatchedCdpDownloads matchedDownloads = waitUntilDownloadsCompleted(driver, fileFilter,
        timeout, incrementTimeout, downloads, options.minimumFileCount());

      return matchedDownloads.files.stream()
        .map(f -> archive(timeout - (currentTimeMillis() - start), contentStrategy, f, config, webDriver))
        .toList();
    }
    finally {
      devTools.clearListeners();
    }
  }

  private File archive(long timeout, ContentStrategy contentStrategy, CdpDownload download, Config config, WebDriver webDriver) {
    return switch (contentStrategy) {
      case FULL_CONTENT -> downloader.copyFileWithTimeout(download.file().getName(),
        () -> archiveFile(config, webDriver, download.file()),
        timeout
      );
      case EMPTY_CONTENT -> downloader.mockFileContent(config, requireNonNull(download.fileName));
    };
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

  private MatchedCdpDownloads waitUntilDownloadsCompleted(
    Driver driver, FileFilter fileFilter, long timeout, long incrementTimeout, CdpDownloads downloads, int minimumFileCount) {

    long pollingInterval = Math.max(driver.config().pollingInterval(), 100);
    long downloadStartedAt = currentTimeMillis();
    Stopwatch stopwatch = new Stopwatch(timeout);
    do {
      MatchedCdpDownloads downloadedFiles = downloads.find(fileFilter);
      if (downloadedFiles.hasAtLeast(minimumFileCount)) {
        log.debug("File download completed after {} ms: {}", stopwatch.getElapsedTimeMs(), downloadedFiles);
        return downloadedFiles;
      }
      else {
        failFastIfNoChanges(downloads, fileFilter, downloadStartedAt, timeout, incrementTimeout);
      }
      stopwatch.sleep(pollingInterval);
    }
    while (!stopwatch.isTimeoutReached());

    List<DownloadedFile> files = downloads.folder().files();
    String message = switch (minimumFileCount) {
      case 1 -> "Failed to download file%s in %s (found %s files: %s)".formatted(
        fileFilter.description(), df.format(timeout), files.size(), files);
      default -> "Failed to download at least %s files%s in %s (found %s files: %s)".formatted(
        minimumFileCount, fileFilter.description(), df.format(timeout), files.size(), files);
    };
    throw new FileNotDownloadedError(message, timeout);
  }

  private DevTools initDevTools(Driver driver) {
    WebDriver webDriver = driver.getWebDriver();

    if (!(webDriver instanceof HasDevTools cdpBrowser)) {
      throw new IllegalArgumentException(
        "The browser you selected \"%s\" doesn't support Chrome Devtools protocol".formatted(driver.browser().name));
    }

    if (!isChromium(webDriver)) {
      throw new IllegalArgumentException(
        "The browser you selected \"%s\" is not Chromium browser".formatted(driver.browser().name));
    }

    DevTools devTools = cdpBrowser.getDevTools();
    devTools.createSessionIfThereIsNotOne(webDriver.getWindowHandle());
    devTools.send(Page.enable(Optional.empty()));
    return devTools;
  }

  private boolean isChromium(WebDriver webDriver) {
    return webDriver instanceof HasCapabilities hasCapabilities &&
      new com.codeborne.selenide.Browser(hasCapabilities.getCapabilities().getBrowserName(), false).isChromium();
  }

  private void prepareDownloadWithCdp(Driver driver, DevTools devTools,
                                      CdpDownloads downloads,
                                      long timeout) {
    devTools.send(Browser.setDownloadBehavior(
      Browser.SetDownloadBehaviorBehavior.DEFAULT,
      Optional.empty(),
      Optional.empty(),
      Optional.of(true)));

    log.debug("clear devtools listeners");
    devTools.clearListeners();
    log.debug("add devtools listener for 'downloadWillBegin'");
    devTools.addListener(downloadWillBegin(), new DownloadWillBeginListener(id(), downloads));
    log.debug("add devtools listener for 'downloadProgress'");
    devTools.addListener(downloadProgress(), new DownloadProgressListener(id(), driver, downloads, timeout));
  }

  private record CdpDownloads(
    DownloadsFolder folder,
    ConcurrentMap<String, CdpDownload> downloads
  ) {
    private MatchedCdpDownloads find(FileFilter fileFilter) {
      return new MatchedCdpDownloads(downloads.values().stream()
        .filter(download -> download.completed)
        .filter(download -> fileFilter.match(download.file()))
        .toList()
      );
    }

    private Optional<Long> lastModificationTime() {
      return downloads.values().stream().map(download -> download.lastModifiedAt).max(Long::compare);
    }

    private void setName(String guid, String fileName) {
      CdpDownload download = download(guid);
      download.fileName = fileName;
      finishIfFileCompleted(download);
    }

    public void inProgress(DownloadProgress e) {
      CdpDownload download = download(e.getGuid());
      download.lastModifiedAt = currentTimeMillis();
      download.fileSize = e.getReceivedBytes().longValue();
      download.expectedFileSize = e.getTotalBytes().longValue();

      finishIfFileCompleted(download);
    }

    private void finishIfFileCompleted(CdpDownload download) {
      if (download.expectedFileSize >= 0 && download.fileSize >= download.expectedFileSize) {
        if (download.fileName != null && download.file().exists()) {
          download.finish();
        }
      }
    }

    public void finish(DownloadProgress e) {
      download(e.getGuid()).finish();
    }

    private synchronized CdpDownload download(String guid) {
      return downloads.computeIfAbsent(guid, __ -> new CdpDownload(folder));
    }
  }

  private record MatchedCdpDownloads(List<CdpDownload> files) {
    public boolean hasAtLeast(int minimumFileCount) {
      return files.size() >= minimumFileCount;
    }

    @Override
    public String toString() {
      return files.stream().map(f -> f.fileName).toList().toString();
    }
  }

  private static class CdpDownload {
    private final DownloadsFolder folder;
    @Nullable
    private String fileName;
    private long fileSize = -1;
    private long expectedFileSize = -1;
    private long lastModifiedAt = currentTimeMillis();
    private boolean completed;

    private CdpDownload(DownloadsFolder folder) {
      this.folder = folder;
    }

    private File file() {
      return new File(folder.getPath(), requireNonNull(fileName));
    }

    private void finish() {
      this.completed = true;
      this.lastModifiedAt = currentTimeMillis();
    }
  }

  private static long id() {
    return SEQUENCE.incrementAndGet();
  }

  private record DownloadWillBeginListener(long id, CdpDownloads downloads) implements Consumer<DownloadWillBegin> {
    @Override
    public void accept(DownloadWillBegin e) {
      log.debug("[{}] Download will begin with suggested file name \"{}\" (url: \"{}\", frameId: {}, guid: {})",
        id, e.getSuggestedFilename(), e.getUrl(), e.getFrameId(), e.getGuid());
      downloads.setName(e.getGuid(), e.getSuggestedFilename());
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "#" + id;
    }
  }

  private record DownloadProgressListener(long id, Driver driver, CdpDownloads downloads, long timeout)
    implements Consumer<DownloadProgress> {
    @Override
    public void accept(DownloadProgress e) {
      log.debug("[{}] Download is {} (received bytes: {}, total bytes: {}, guid: {})",
        id, e.getState(), e.getReceivedBytes(), e.getTotalBytes(), e.getGuid());

      switch (e.getState()) {
        case CANCELED -> {
          String message = "File download is %s (received bytes: %s, total bytes: %s, guid: %s)".formatted(
            e.getState(), e.getReceivedBytes(), e.getTotalBytes(), e.getGuid());
          throw new FileNotDownloadedError(message, timeout);
        }
        case COMPLETED -> downloads.finish(e);
        case INPROGRESS -> downloads.inProgress(e);
      }
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "#" + id;
    }
  }

  private void failFastIfNoChanges(CdpDownloads downloads, FileFilter filter,
                                   long downloadStartedAt, long timeout, long incrementTimeout) {
    long now = currentTimeMillis();
    long lastModifiedAt = downloads.lastModificationTime().orElse(downloadStartedAt);
    long filesHasNotBeenUpdatedForMs = now - lastModifiedAt;
    if (filesHasNotBeenUpdatedForMs > incrementTimeout) {
      String message = String.format("Failed to download file%s in %s: files in %s haven't been modified for %s " +
          "(lastUpdate: %s, now: %s, incrementTimeout: %s)",
        filter.description(), df.format(timeout), downloads.folder, df.format(filesHasNotBeenUpdatedForMs),
        lastModifiedAt, now, df.format(incrementTimeout));
      throw new FileNotDownloadedError(message, timeout);
    }
  }
}
