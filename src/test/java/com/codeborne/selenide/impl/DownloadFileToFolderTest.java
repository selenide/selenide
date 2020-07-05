package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.files.FileFilters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class DownloadFileToFolderTest {
  private final Waiter waiter = new DummyWaiter();
  private final WindowsCloser windowsCloser = spy(new DummyWindowsCloser());
  private final DownloadFileToFolder command = new DownloadFileToFolder(waiter, windowsCloser);
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webdriver = mock(WebDriver.class);
  private final WebElementSource linkWithHref = mock(WebElementSource.class);
  private final WebElement link = mock(WebElement.class);
  private final DriverStub driver = new DriverStub(config, new Browser("opera", false), webdriver, null);

  @BeforeEach
  void setUp() {
    when(linkWithHref.driver()).thenReturn(driver);
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  @Test
  void tracksForNewFilesInDownloadsFolder() throws IOException {
    String newFileName = UUID.randomUUID().toString() + ".txt";
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(new File(driver.browserDownloadsFolder(), newFileName), "Hello Bingo-Bongo", UTF_8);
      return null;
    }).when(link).click();

    File downloadedFile = command.download(linkWithHref, link, 3000, FileFilters.none());

    assertThat(downloadedFile.getName()).isEqualTo(newFileName);
    assertThat(downloadedFile.getParentFile().getAbsolutePath()).isEqualTo(driver.browserDownloadsFolder().getAbsolutePath());
    assertThat(readFileToString(downloadedFile, UTF_8)).isEqualTo("Hello Bingo-Bongo");
  }
}
