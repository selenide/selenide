package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static integration.SelenoidSetup.checkDownload;
import static integration.SelenoidSetup.resetSelenoidSettings;

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
  void downloadFileInLocalBrowser() throws IOException {
    resetSelenoidSettings();
    Configuration.headless = true;
    Configuration.fileDownload = FOLDER;
    checkDownload();
  }
}
