package org.selenide.selenoid;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.Downloader;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.impl.FileHelper.moveFile;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;


@ParametersAreNonnullByDefault
public class DownloadFileInSelenoid extends com.codeborne.selenide.impl.DownloadFileToFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileInSelenoid.class);
  private final Downloader downloader;

  public DownloadFileInSelenoid() {
    this(new Downloader());
  }

  DownloadFileInSelenoid(Downloader downloader) {
    this.downloader = downloader;
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter) throws FileNotFoundException {

    Driver driver = anyClickableElement.driver();
    Config config = driver.config();
    if (config.remote() == null) {
      log.debug("Working in local browser. Switching to a default FOLDER implementation.");
      return super.download(anyClickableElement, clickable, timeout, fileFilter);
    }

    RemoteWebDriver webDriver = (RemoteWebDriver) driver.getWebDriver();
    SelenoidClient selenoidClient = new SelenoidClient(config.remote(), webDriver.getSessionId().toString());

    selenoidClient.deleteDownloadedFiles();
    clickable.click();

    Optional<String> downloadedFileName = waitForDownloads(selenoidClient, config, timeout, fileFilter);
    if (!downloadedFileName.isPresent()) {
      throw new FileNotFoundException("Failed to download file " + anyClickableElement + " in " + timeout + " ms.");
    }

    File downloadedFile = selenoidClient.download(downloadedFileName.get());
    return archiveFile(driver.config(), downloadedFile);
  }

  @CheckReturnValue
  @Nonnull
  private Optional<String> waitForDownloads(SelenoidClient selenoidClient, Config config, long timeout, FileFilter fileFilter) {
    sleep(config.pollingInterval());

    List<String> fileNames = emptyList();
    Optional<String> matchingFile = Optional.empty();

    for (long start = currentTimeMillis(); currentTimeMillis() - start <= timeout; ) {
      fileNames = selenoidClient.downloads();
      matchingFile = firstMatchingFile(fileNames, fileFilter);
      if (matchingFile.isPresent()) break;
      sleep(config.pollingInterval());
    }

    log.debug("All downloaded files: {}", fileNames);
    return matchingFile;
  }

  private Optional<String> firstMatchingFile(List<String> fileNames, FileFilter fileFilter) {
    return fileNames.stream()
        .filter(fileName -> fileFilter.match(new DownloadedFile(new File(fileName), emptyMap())))
        .findFirst();
  }

  @CheckReturnValue
  @Nonnull
  private File archiveFile(Config config, File downloadedFile) {
    File uniqueFolder = downloader.prepareTargetFolder(config);
    File archivedFile = new File(uniqueFolder, downloadedFile.getName());
    moveFile(downloadedFile, archivedFile);
    return archivedFile;
  }
}
