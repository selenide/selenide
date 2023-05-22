package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AppiumScreenSourceExtractorTest {
  @ParameterizedTest
  @ValueSource(classes = {AndroidDriver.class, IOSDriver.class})
  void shouldUseXMLFileExtension_forMobileDriver(Class<WebDriver> webdriver) {
    WebDriver driver = mock(webdriver);
    Config config = new SelenideConfig().reportsFolder("foo");
    File sourceFile = new AppiumScreenSourceExtractor().createFile(config, driver, "test123");
    assertThat(sourceFile.getName()).isEqualTo("test123.xml");
  }

  @ParameterizedTest
  @ValueSource(classes = {ChromeDriver.class, FirefoxDriver.class, EdgeDriver.class})
  void shouldUseHTMLFileExtension_forWebBrowser(Class<WebDriver> webdriver) {
    WebDriver driver = mock(webdriver);
    Config config = new SelenideConfig().reportsFolder("foo");
    File sourceFile = new AppiumScreenSourceExtractor().createFile(config, driver, "test456");
    assertThat(sourceFile.getName()).isEqualTo("test456.html");
  }
}
