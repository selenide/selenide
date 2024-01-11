package com.codeborne.selenide.impl;

import com.codeborne.selenide.*;
import com.codeborne.selenide.files.FileFilters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.files.DownloadActions.click;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

final class DownloadFileToFolderCdpTest {
  private final Downloader downloader = new Downloader();
  private final DownloadFileToFolderCdp command = new DownloadFileToFolderCdp(downloader);
  private final SelenideConfig config = new SelenideConfig();
  private final WebDriver webdriver = setUpDriver();
  private final WebElementSource linkWithHref = mock();
  private final WebElement link = mock();
  private final BrowserDownloadsFolder downloadsFolder = new SharedDownloadsFolder("build/downloads/" + randomUUID());
  private final DriverStub driver =
    new DriverStub(config, new Browser("chrome", false), webdriver, null, downloadsFolder);

  @BeforeEach
  void setUp() {
    when(linkWithHref.driver()).thenReturn(driver);
    when(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link);
    when(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>");
  }

  private static WebDriver setUpDriver() {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--headless=new");
    return new ChromeDriver(chromeOptions);
  }

  @Test
  void tracksForNewFilesInDownloadsFolder() throws IOException {
    String newFileName = "bingo-bongo.txt";
    doAnswer((Answer<Void>) i -> {
      writeStringToFile(downloadsFolder.file(newFileName), "Hello Bingo-Bongo", UTF_8);
      return null;
    }).when(link).click();

    File downloadedFile = command.download(linkWithHref, link, 3000, 300,
      FileFilters.none(), click());

    assertThat(downloadedFile.getName()).isEqualTo(newFileName);
    assertThat(downloadedFile.getParentFile()).isNotEqualTo(downloadsFolder.getFolder());
    assertThat(readFileToString(downloadedFile, UTF_8)).isEqualTo("Hello Bingo-Bongo");
  }
}
