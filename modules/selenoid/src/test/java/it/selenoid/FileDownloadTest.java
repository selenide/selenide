package it.selenoid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static it.selenoid.SelenoidSetup.checkDownload;
import static it.selenoid.SelenoidSetup.resetSelenoidSettings;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SelenoidSetup.class)
public class FileDownloadTest {
  @Test
  void downloadFileInSelenoid_using_httpGet() throws IOException {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
    checkDownload();
  }

  @Test
  void downloadFileInSelenoid_using_folder() throws IOException {
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = FOLDER;
    checkDownload();
  }

  @Test
  void downloadFileInSelenoid_using_proxy() throws IOException {
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = PROXY;
    checkDownload();
  }

  @Test
  void slowDownloadToFolder() throws IOException {
    String fileContent = rightPad("Lorem ipsum dolor sit amet", 4096, "\nlaborum");
    open("https://selenide.org/test-page/download.html");
    $("[name=delay]").setValue("3000");
    $("#lore-ipsum").setValue(fileContent);

    File file = $("#slow-download").download(using(FOLDER).withFilter(withExtension("txt")));

    assertThat(file).hasName("hello.txt");
    assertThat(file).content().isEqualToIgnoringWhitespace(fileContent);
  }

  @Test
  void downloadFileInLocalBrowser() throws IOException {
    resetSelenoidSettings();
    Configuration.headless = true;
    Configuration.fileDownload = FOLDER;
    checkDownload();
  }
}
