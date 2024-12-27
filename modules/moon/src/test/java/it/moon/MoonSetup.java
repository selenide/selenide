package it.moon;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;

public class MoonSetup implements BeforeEachCallback, AfterEachCallback  {
  @Override
  public void beforeEach(final ExtensionContext context) {
    closeWebDriver();
    Configuration.baseUrl = "https://selenide.org/test-page";
    Configuration.browserCapabilities = capabilities();
    Configuration.browser = "chrome";
    Configuration.remote = moonUrl();
    Configuration.headless = false;
    Configuration.fileDownload = FileDownloadMode.HTTPGET;
    Configuration.proxyEnabled = false;
  }

  static String moonUrl() {
    return "http://moon.aerokube.local/wd/hub";
  }

  static void resetMoonSettings() {
    Configuration.remote = null;
    Configuration.browserCapabilities = new DesiredCapabilities();
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    closeWebDriver();
    resetMoonSettings();
  }

  static MutableCapabilities capabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName("chrome");
    capabilities.setCapability("browserVersion", "124.0.6367.60-1");
    capabilities.setCapability("moon:options", Map.of(
      "enableVNC", true,
      "enableVideo", true
    ));
    return capabilities;
  }

  @CanIgnoreReturnValue
  static void checkDownload() throws IOException {
    open("/download.html");
    File file = $(byText("hello-world.txt")).download(withExtension("txt"));
    assertThat(file).hasName("hello-world.txt");
    assertThat(readFileToString(file, UTF_8)).isEqualTo("Hello, world!");
  }
}
