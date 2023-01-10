package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.DummyWebDriver;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.files.FileFilters;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.files.DownloadActions.click;
import static com.codeborne.selenide.impl.DownloadFileToFolder.isFileModifiedLaterThan;
import static java.io.File.createTempFile;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

final class DownloadFileToFolderTest {
  private final Downloader downloader = new Downloader();
  private final WindowsCloser windowsCloser = spy(new DummyWindowsCloser());
  private final DownloadFileToFolder command = new DownloadFileToFolder(downloader, windowsCloser);
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webdriver = new DummyWebDriver();
  private final WebElementSource linkWithHref = mock();
  private final WebElement link = mock();
  private final DriverStub driver = new DriverStub(config, new Browser("opera", false), webdriver, null);

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
      writeStringToFile(driver.browserDownloadsFolder().file(newFileName), "Hello Bingo-Bongo", UTF_8);
      return null;
    }).when(link).click();

    File downloadedFile = command.download(linkWithHref, link, 3000, 300, FileFilters.none(), click());

    assertThat(downloadedFile.getName()).isEqualTo(newFileName);
    assertThat(downloadedFile.getParentFile()).isNotEqualTo(driver.browserDownloadsFolder().toFile());
    assertThat(readFileToString(downloadedFile, UTF_8)).isEqualTo("Hello Bingo-Bongo");
  }

  @Test
  void fileModificationCheck() throws IOException {
    assertThat(isFileModifiedLaterThan(file(1597333000L), 1597333000L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1597333000L), 1597332999L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1597333000L), 1597334001L)).isFalse();
  }

  @Test
  void fileModificationCheck_worksWithSecondsPrecision() throws IOException {
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111111000L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111111999L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111112000L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111112001L)).isFalse();
  }

  @Test
  void fileModificationCheck_worksEvenIfFileModificationTime_isInPreviousSecond() throws IOException {
    assertThat(isFileModifiedLaterThan(file(1111111112999L), 1111111113004L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111114998L), 1111111115002L)).isTrue();
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

  private File file(long modifiedAt) throws IOException {
    File file = createTempFile("selenide-tests", "new-file");
    FileUtils.touch(file);
    if (!file.setLastModified(modifiedAt)) {
      throw new IllegalStateException("Failed to set last modified time to file " + file.getAbsolutePath());
    }
    return file;
  }
}
