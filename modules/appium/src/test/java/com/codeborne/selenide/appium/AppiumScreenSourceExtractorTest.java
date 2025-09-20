package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import io.appium.java_client.AppiumDriver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AppiumScreenSourceExtractorTest {
  private final Config config = new SelenideConfig().reportsFolder("foo");
  private final AppiumScreenSourceExtractor extractor = new AppiumScreenSourceExtractor();

  @ParameterizedTest
  @MethodSource("mobileDrivers")
  void shouldUseXMLFileExtension_forMobileDriver(AppiumDriver driver) {
    File sourceFile = extractor.createFile(config, driver, "test123");
    assertThat(sourceFile.getName()).isEqualTo("test123.xml");
  }

  @ParameterizedTest
  @MethodSource("webBrowsers")
  void shouldUseHTMLFileExtension_forWebBrowser(Browser browser) {
    WebDriver webDriver = new FakeWebDriver(new DesiredCapabilities(), browser);

    File sourceFile = extractor.createFile(config, webDriver, "test456");
    assertThat(sourceFile.getName()).isEqualTo("test456.html");
  }

  private static Stream<Arguments> mobileDrivers() {
    return Stream.of(
      Arguments.of(new FakeAndroidDriver(new DesiredCapabilities())),
      Arguments.of(new FakeIOSDriver(new DesiredCapabilities()))
    );
  }

  static Stream<Arguments> webBrowsers() {
    return AppiumDriverUnwrapperTest.webBrowsers();
  }
}
