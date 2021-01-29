package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;

public class FileDownloadTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName("chrome");
    capabilities.setVersion("86.0");
    capabilities.setCapability("enableVNC", true);
    capabilities.setCapability("enableVideo", true);
    capabilities.setCapability("enableLog", true);
    Configuration.browserCapabilities = capabilities;
    Configuration.fileDownload = FileDownloadMode.FOLDER;
  }

  @Test
  void downloadFileInSelenoid() throws IOException {
    Configuration.remote = "http://localhost:4444/wd/hub";
    open("https://the-internet.herokuapp.com/download");

    File file = $(byText("some-file.txt")).download(withExtension("txt"));

    assertThat(file.getName()).isEqualTo("some-file.txt");
    assertThat(readFileToString(file, UTF_8)).startsWith("{\\rtf");
  }

  @Test
  void downloadFileInLocalBrowser() throws IOException {
    Configuration.remote = null;
    open("https://the-internet.herokuapp.com/download");

    File file = $(byText("some-file.txt")).download(withExtension("txt"));

    assertThat(file.getName()).isEqualTo("some-file.txt");
    assertThat(readFileToString(file, UTF_8)).startsWith("{\\rtf");
  }
}
