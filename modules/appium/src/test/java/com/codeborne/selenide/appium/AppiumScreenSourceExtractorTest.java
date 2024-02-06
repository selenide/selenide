package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppiumScreenSourceExtractorTest {
  @ParameterizedTest
  @ValueSource(classes = {AndroidDriver.class, IOSDriver.class})
  void shouldUseXMLFileExtension_forMobileDriver(Class<AppiumDriver> webdriver) {
    AppiumDriver driver = mock(webdriver);
    when(driver.getCapabilities()).thenReturn(new DesiredCapabilities());
    Config config = new SelenideConfig().reportsFolder("foo");
    File sourceFile = new AppiumScreenSourceExtractor().createFile(config, driver, "test123");
    assertThat(sourceFile.getName()).isEqualTo("test123.xml");
  }

  @ParameterizedTest
  @ValueSource(classes = {ChromeDriver.class, FirefoxDriver.class, EdgeDriver.class})
  void shouldUseHTMLFileExtension_forWebBrowser(Class<RemoteWebDriver> webdriver) {
    RemoteWebDriver driver = mock(webdriver);
    when(driver.getCapabilities()).thenReturn(new DesiredCapabilities());
    Config config = new SelenideConfig().reportsFolder("foo");
    File sourceFile = new AppiumScreenSourceExtractor().createFile(config, driver, "test456");
    assertThat(sourceFile.getName()).isEqualTo("test456.html");
  }
}
