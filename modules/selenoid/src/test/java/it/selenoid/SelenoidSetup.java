package it.selenoid;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
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

public class SelenoidSetup implements BeforeEachCallback, AfterEachCallback  {
  @Override
  public void beforeEach(final ExtensionContext context) {
    closeWebDriver();
    System.setProperty("webdriver.http.factory", "jdk-http-client");
    Configuration.baseUrl = "https://selenide.org/test-page";
    Configuration.browserCapabilities = capabilities();
    Configuration.browser = "chrome";
    Configuration.remote = selenoidUrl();
    Configuration.headless = false;
    Configuration.fileDownload = FileDownloadMode.HTTPGET;
    Configuration.proxyEnabled = false;
  }

  static String selenoidUrl() {
    return "http://localhost:4444/wd/hub";
  }

  static void resetSelenoidSettings() {
    Configuration.remote = null;
    Configuration.browserCapabilities = new DesiredCapabilities();
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    closeWebDriver();
    resetSelenoidSettings();
  }

  static DesiredCapabilities capabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName("chrome");
    capabilities.setCapability("selenoid:options", ImmutableMap.of(
      "enableVNC", true,
      "enableVideo", true
    ));
    return capabilities;
  }

  static void checkDownload() throws IOException {
    open("/download.html");
    File file = $(byText("hello-world.txt")).download(withExtension("txt"));
    assertThat(file).hasName("hello-world.txt");
    assertThat(readFileToString(file, UTF_8)).isEqualTo("Hello, world!");
  }
}
