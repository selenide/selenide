package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.BrowserDownloadsFolder;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.DummyWebDriver;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SharedDownloadsFolder;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadedFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;
import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class DownloadFileToFolderTest {
  private final Downloader downloader = new Downloader();
  private final DownloadFileToFolder command = new DownloadFileToFolder(downloader);
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webdriver = new DummyWebDriver();
  private final WebElementSource linkWithHref = mock();
  private final WebElement link = mock();
  private final BrowserDownloadsFolder downloadsFolder = new SharedDownloadsFolder("build/downloads/" + randomUUID());
  private final DriverStub driver = new DriverStub(config, new Browser("opera", false), webdriver, null, downloadsFolder);

  @BeforeEach
  void setUp() {
    when(linkWithHref.driver()).thenReturn(driver);
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  @Test
  void tracksForNewFilesInDownloadsFolder() throws IOException {
    String newFileName = "bingo-bongo.txt";
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file(newFileName), "Hello Bingo-Bongo", UTF_8);
      return null;
    }).when(link).click();

    List<File> downloadedFiles = command.download(driver, link, 3000, 300, file().withMethod(FOLDER));

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo(newFileName);
    assertThat(downloadedFiles.get(0).getParentFile()).isNotEqualTo(downloadsFolder.getFolder());
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("Hello Bingo-Bongo");
  }

  @ParameterizedTest
  @MethodSource("staleTemporaryFiles")
  void ignoresTemporaryFilesFromPreviousFailedDownload(String browser, String staleTemporaryFile) throws IOException {
    config.pollingInterval(50);
    DriverStub driver = driver(browser, downloadsFolder);
    writeStringToFile(downloadsFolder.file(staleTemporaryFile), "Partial old download", UTF_8);
    clickCreates(downloadsFolder, "document.pdf", "%PDF new file");

    List<File> downloadedFiles = command.download(driver, link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf"));

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo("document.pdf");
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("%PDF new file");
  }

  @Test
  void continuesDownloadWhenLocalCleanupFailsWithIoException() throws IOException {
    config.pollingInterval(50);
    FailingIoCleanupDownloadsFolder downloadsFolder = new FailingIoCleanupDownloadsFolder("build/downloads/" + randomUUID());
    DriverStub driver = driver(CHROME, downloadsFolder);
    writeStringToFile(downloadsFolder.file("old-file.crdownload"), "Partial old download", UTF_8);
    clickCreates(downloadsFolder, "document.pdf", "%PDF new file");

    List<File> downloadedFiles = command.download(driver, link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf"));

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo("document.pdf");
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("%PDF new file");
    assertThat(downloadsFolder.cleanupAttempts).isEqualTo(1);
  }

  @Test
  void detectsOverwrittenFileAfterLocalCleanupFailure() throws IOException {
    config.pollingInterval(50);
    FailingIoCleanupDownloadsFolder downloadsFolder = new FailingIoCleanupDownloadsFolder("build/downloads/" + randomUUID());
    DriverStub driver = driver(CHROME, downloadsFolder);
    writeStringToFile(downloadsFolder.file("document.pdf"), "old", UTF_8);
    writeStringToFile(downloadsFolder.file("old-file.crdownload"), "Partial old download", UTF_8);
    clickCreates(downloadsFolder, "document.pdf", "%PDF new file");

    List<File> downloadedFiles = command.download(driver, link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf"));

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo("document.pdf");
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("%PDF new file");
  }

  @Test
  void propagatesRemoteCleanupFailure() {
    FailingIoCleanupDownloadsFolder downloadsFolder = new FailingIoCleanupDownloadsFolder("build/downloads/" + randomUUID());
    DriverStub driver = remoteDriver(CHROME, downloadsFolder);

    assertThatThrownBy(() -> command.download(driver, link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf")))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Simulated cleanup failure");
    verify(link, never()).click();
  }

  @Test
  void propagatesUnexpectedLocalCleanupFailure() {
    FailingCleanupDownloadsFolder downloadsFolder = new FailingCleanupDownloadsFolder("build/downloads/" + randomUUID());
    DriverStub driver = driver(CHROME, downloadsFolder);

    assertThatThrownBy(() -> command.download(driver, link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf")))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Simulated cleanup failure");
    verify(link, never()).click();
  }

  @Test
  void propagatesListingFailureAfterRecoverableCleanupFailure() {
    FailingFilesAfterCleanupFolder downloadsFolder = new FailingFilesAfterCleanupFolder("build/downloads/" + randomUUID());
    DriverStub driver = driver(CHROME, downloadsFolder);

    assertThatThrownBy(() -> command.download(driver, link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf")))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Simulated cleanup failure")
      .satisfies(error -> {
        assertThat(error.getSuppressed()).hasSize(1);
        assertThat(error.getSuppressed()[0])
          .isInstanceOf(UncheckedIOException.class)
          .hasMessage("Failed to list files");
      });
    verify(link, never()).click();
  }

  @Test
  void ignoresTemporaryFileFromPreviousFailedDownloadIfItChangesDuringClick() throws IOException {
    config.pollingInterval(50);
    writeStringToFile(downloadsFolder.file("image.iso.crdownload"), "Partial old download", UTF_8);
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("image.iso.crdownload"), "Partial old download still growing", UTF_8);
      writeStringToFile(downloadsFolder.file("document.pdf"), "%PDF new file", UTF_8);
      return null;
    }).when(link).click();

    List<File> downloadedFiles = command.download(driver(CHROME, downloadsFolder), link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf"));

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo("document.pdf");
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("%PDF new file");
  }

  @Test
  void failsFastIfOnlyStaleTemporaryFileKeepsChanging() {
    config.pollingInterval(50);
    ChangingStaleTemporaryDownloadsFolder downloadsFolder =
      new ChangingStaleTemporaryDownloadsFolder("build/downloads/" + randomUUID());

    assertThatThrownBy(() -> command.download(driver(CHROME, downloadsFolder), link, 1500, 200,
      file().withMethod(FOLDER).withName("document.pdf")))
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("haven't been modified");
  }

  @ParameterizedTest
  @MethodSource("expectedFilesWithTemporaryExtensions")
  void detectsExpectedTemporaryLikeFileAfterLocalCleanupFailure(String browser, String fileName, DownloadOptions options)
    throws IOException {
    config.pollingInterval(50);
    FailingIoCleanupDownloadsFolder downloadsFolder = new FailingIoCleanupDownloadsFolder("build/downloads/" + randomUUID());
    DriverStub driver = driver(browser, downloadsFolder);
    writeStringToFile(downloadsFolder.file(fileName), "old", UTF_8);
    clickCreates(downloadsFolder, fileName, "actual downloaded file");

    List<File> downloadedFiles = command.download(driver, link, 1500, 1000, options);

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo(fileName);
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("actual downloaded file");
  }

  @ParameterizedTest
  @MethodSource("sameFinalNameFilters")
  void detectsExpectedFileEvenIfPreviousTemporaryFileHadSameFinalName(DownloadOptions options) throws IOException {
    config.pollingInterval(50);
    writeStringToFile(downloadsFolder.file("document.pdf.crdownload"), "Partial old download", UTF_8);
    clickCreates(downloadsFolder, "document.pdf", "%PDF new file");

    List<File> downloadedFiles = command.download(driver(CHROME, downloadsFolder), link, 1500, 1000, options);

    assertThat(downloadedFiles).hasSize(1);
    assertThat(downloadedFiles.get(0).getName()).isEqualTo("document.pdf");
    assertThat(readFileToString(downloadedFiles.get(0), UTF_8)).isEqualTo("%PDF new file");
  }

  @Test
  void waitWhileFilesAreBeingModifiedReceivesOriginalFolder() throws IOException {
    config.pollingInterval(50);
    HookCapturingDownloadFileToFolder command = new HookCapturingDownloadFileToFolder(downloader);
    writeStringToFile(downloadsFolder.file("stale.tmp"), "old file", UTF_8);
    clickCreates(downloadsFolder, "document.pdf", "%PDF new file");

    List<File> downloadedFiles = command.download(driver("opera", downloadsFolder), link, 1500, 1000,
      file().withMethod(FOLDER).withName("document.pdf"));

    assertThat(downloadedFiles).hasSize(1);
    assertThat(command.folderSeenByWaitWhileBeingModified).isSameAs(downloadsFolder);
  }

  @Test
  void filesHasNotBeenUpdatedForMs() {
    assertThat(command.filesHasNotBeenUpdatedForMs(1111111114000L, 1111111114998L, 1111111114998L)).isEqualTo(0);
    assertThat(command.filesHasNotBeenUpdatedForMs(1111111114000L, 1111111114998L, 1111111114000L)).isEqualTo(998);

    assertThat(command.filesHasNotBeenUpdatedForMs(1111111114000L, 1111111114998L, 1111111113333L))
      .as("File modification time may be in the past because of file system accuracy (up to 1 second error)")
      .isEqualTo(998);

    assertThat(command.filesHasNotBeenUpdatedForMs(1111111114000L, 1111111114998L, 0))
      .as("File modification time may be 0 (if file path is treated as invalid for some reason)")
      .isEqualTo(998);
  }

  private DriverStub driver(String browser, BrowserDownloadsFolder downloadsFolder) {
    return new DriverStub(config, new Browser(browser, false), webdriver, null, downloadsFolder);
  }

  private DriverStub remoteDriver(String browser, BrowserDownloadsFolder downloadsFolder) {
    SelenideConfig remoteConfig = new SelenideConfig().remote("http://example.com:4444/wd/hub");
    return new DriverStub(remoteConfig, new Browser(browser, false), webdriver, null, downloadsFolder);
  }

  private void clickCreates(BrowserDownloadsFolder downloadsFolder, String name, String content) {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file(name), content, UTF_8);
      return null;
    }).when(link).click();
  }

  private static Stream<Arguments> staleTemporaryFiles() {
    return Stream.of(
      Arguments.of(CHROME, "stale.crdownload"),
      Arguments.of(CHROME, "stale.tmp"),
      Arguments.of(EDGE, "stale.crdownload"),
      Arguments.of(EDGE, "stale.tmp"),
      Arguments.of(FIREFOX, "stale.part")
    );
  }

  private static Stream<DownloadOptions> sameFinalNameFilters() {
    return Stream.of(
      file().withMethod(FOLDER),
      file().withMethod(FOLDER).withExtension("pdf"),
      file().withMethod(FOLDER).withName("document.pdf")
    );
  }

  private static Stream<Arguments> expectedFilesWithTemporaryExtensions() {
    return Stream.of(
      Arguments.of(CHROME, "report.crdownload", file().withMethod(FOLDER).withName("report.crdownload")),
      Arguments.of(CHROME, "report.tmp", file().withMethod(FOLDER).withName("report.tmp")),
      Arguments.of(FIREFOX, "report.part", file().withMethod(FOLDER).withName("report.part"))
    );
  }

  private static class FailingCleanupDownloadsFolder extends SharedDownloadsFolder {
    protected int cleanupAttempts;

    FailingCleanupDownloadsFolder(String folder) {
      super(folder);
    }

    @Override
    public void cleanupBeforeDownload() {
      cleanupAttempts++;
      throw new IllegalStateException("Simulated cleanup failure");
    }
  }

  private static class FailingIoCleanupDownloadsFolder extends FailingCleanupDownloadsFolder {
    FailingIoCleanupDownloadsFolder(String folder) {
      super(folder);
    }

    @Override
    public void cleanupBeforeDownload() {
      cleanupAttempts++;
      throw new IllegalStateException("Simulated cleanup failure", new IOException("File is busy"));
    }
  }

  private static final class FailingFilesAfterCleanupFolder extends FailingIoCleanupDownloadsFolder {
    FailingFilesAfterCleanupFolder(String folder) {
      super(folder);
    }

    @Override
    public List<DownloadedFile> files() {
      throw new UncheckedIOException("Failed to list files", new IOException("Cannot read folder"));
    }
  }

  private static final class ChangingStaleTemporaryDownloadsFolder extends SharedDownloadsFolder {
    ChangingStaleTemporaryDownloadsFolder(String folder) {
      super(folder);
    }

    @Override
    public List<DownloadedFile> files() {
      return List.of(new DownloadedFile(file("image.iso.crdownload"), System.currentTimeMillis(), 1024, Map.of()));
    }
  }

  private static final class HookCapturingDownloadFileToFolder extends DownloadFileToFolder {
    private DownloadsFolder folderSeenByWaitWhileBeingModified;

    HookCapturingDownloadFileToFolder(Downloader downloader) {
      super(downloader);
    }

    @Override
    protected void waitWhileFilesAreBeingModified(Driver driver, DownloadsFolder folder, long timeout, long pollingInterval) {
      folderSeenByWaitWhileBeingModified = folder;
    }
  }
}
