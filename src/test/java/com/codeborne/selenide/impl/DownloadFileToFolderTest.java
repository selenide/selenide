package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.BrowserDownloadsFolder;
import com.codeborne.selenide.DownloadFilesOptions;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.DummyWebDriver;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SharedDownloadsFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    File downloadedFile = command.download(linkWithHref, link, 3000, 300, file().withMethod(FOLDER));

    assertThat(downloadedFile.getName()).isEqualTo(newFileName);
    assertThat(downloadedFile.getParentFile()).isNotEqualTo(downloadsFolder.getFolder());
    assertThat(readFileToString(downloadedFile, UTF_8)).isEqualTo("Hello Bingo-Bongo");
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

  @Test
  void downloadFilesReturnsAllNewMatchingFilesSortedByMtime() throws IOException {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("first.txt"), "1", UTF_8);
      writeStringToFile(downloadsFolder.file("second.txt"), "22", UTF_8);
      writeStringToFile(downloadsFolder.file("third.txt"), "333", UTF_8);
      return null;
    }).when(link).click();

    List<File> files = command.downloadFiles(linkWithHref, link, 3000, 300,
      DownloadFilesOptions.files(3).withMethod(FOLDER));

    assertThat(files).hasSize(3);
    assertThat(files).extracting(File::getName)
      .containsExactlyInAnyOrder("first.txt", "second.txt", "third.txt");
    for (File file : files) {
      assertThat(file.getParentFile()).isNotEqualTo(downloadsFolder.getFolder());
    }
    // All three end up in the same per-call archive folder
    assertThat(files.stream().map(File::getParentFile).distinct().toList()).hasSize(1);
  }

  @Test
  void downloadFilesAppliesFilter() throws IOException {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("report.pdf"), "pdf", UTF_8);
      writeStringToFile(downloadsFolder.file("notes.txt"), "txt", UTF_8);
      writeStringToFile(downloadsFolder.file("summary.pdf"), "pdf2", UTF_8);
      return null;
    }).when(link).click();

    List<File> files = command.downloadFiles(linkWithHref, link, 3000, 300,
      DownloadFilesOptions.files(2).withMethod(FOLDER).withExtension("pdf"));

    assertThat(files).extracting(File::getName)
      .containsExactlyInAnyOrder("report.pdf", "summary.pdf");
  }

  @Test
  void downloadFilesFailsFastIfTooManyMatchingFilesAppear() throws IOException {
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file("a.txt"), "a", UTF_8);
      writeStringToFile(downloadsFolder.file("b.txt"), "b", UTF_8);
      writeStringToFile(downloadsFolder.file("c.txt"), "c", UTF_8);
      return null;
    }).when(link).click();

    assertThatThrownBy(() ->
      command.downloadFiles(linkWithHref, link, 3000, 300,
        DownloadFilesOptions.files(2).withMethod(FOLDER))
    )
      .isInstanceOf(com.codeborne.selenide.ex.FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files");
  }
}
