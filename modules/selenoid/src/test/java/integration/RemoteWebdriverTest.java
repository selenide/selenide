package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoteWebdriverTest {

  @BeforeEach
  void setUp() {
    Selenide.closeWebDriver();
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
  }

  @Test
  void canStartRemoteWebDriver() throws IOException {
    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new ChromeOptions(), false);
    setWebDriver(driver);
    open("https://the-internet.herokuapp.com/download");
    File file = $(byText("some-file.txt")).download(withExtension("txt"));
    assertThat(file).hasName("some-file.txt");
    assertThat(readFileToString(file, UTF_8)).startsWith("{\\rtf");
  }

  @AfterEach
  void tearDown() {
    closeWebDriver();
  }
}
