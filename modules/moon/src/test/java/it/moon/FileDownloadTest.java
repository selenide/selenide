package it.moon;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.CDP;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static it.moon.MoonSetup.checkDownload;
import static it.moon.MoonSetup.resetMoonSettings;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@ExtendWith(MoonSetup.class)
public class FileDownloadTest {
  @Test
  void downloadFileFromMoon_using_httpGet() throws IOException {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
    checkDownload();
  }

  @Test
  void downloadFileFromMoon_using_folder() throws IOException {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = FOLDER;
    checkDownload();
  }

  @Test
  void downloadFileFromMoon_using_cdp() throws IOException {
    assumeThat(isFirefox())
      .as("Firefox doesn't support CDP download method")
      .isFalse();

    Configuration.proxyEnabled = false;
    Configuration.fileDownload = CDP;
    checkDownload();
  }

  @Test
  void downloadFileFromMoon_using_proxy() throws IOException {
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = PROXY;
    checkDownload();
  }

  @Test
  void slowDownloadToFolder() {
    String fileContent = rightPad("Lorem ipsum dolor sit amet", 4096, "\nlaborum");
    open("https://selenide.org/test-page/download.html");
    $("[name=delay]").setValue("3000");
    $("#lore-ipsum").setValue(fileContent);

    File file = $("#slow-download").download(using(FOLDER).withExtension("txt"));

    assertThat(file).hasName("hello.txt");
    assertThat(file).content().isEqualToIgnoringWhitespace(fileContent);
  }

  @Test
  void downloadFileInLocalBrowser() throws IOException {
    resetMoonSettings();
    Configuration.headless = true;
    Configuration.fileDownload = FOLDER;
    checkDownload();
  }
}
