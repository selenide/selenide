package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.WebDriverRunner.FIREFOX;
import static com.codeborne.selenide.WebDriverRunner.INTERNET_EXPLORER_FULL_NAME;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DriverTypeChecksAtRuntimeForIETest {

  @After
  public void resetSettings() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = System.getProperty("browser", FIREFOX);
  }


  @Test
  public void internetExplorerDriverSetToWebDriverRunnerTest() {
    WebDriver ieDriver = mock(InternetExplorerDriver.class);
    WebDriverRunner.setWebDriver(ieDriver);

    assertThat(isIE(), is(true));
  }

  @Test
  public void firefoxDriverSetToWebDriverRunnerTest() {
    WebDriver ffDriver = mock(FirefoxDriver.class);
    WebDriverRunner.setWebDriver(ffDriver);

    assertThat(isIE(), is(false));
  }

  @Test
  public void webDriverProviderReturnsInternetExplorerDriverInstanceTest() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = LocalDriverProvider.class.getName();
    LocalDriverProvider.driverClass = InternetExplorerDriver.class;

    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(true));
  }

  @Test
  public void webDriverProviderReturnsFirefoxDriverInstanceTest() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = LocalDriverProvider.class.getName();
    LocalDriverProvider.driverClass = FirefoxDriver.class;

    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(false));
  }

  @Test
  public void webDriverProviderReturnsRemoteDriverInstanceTest() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = RemoteDriverProvider.class.getName();

    RemoteDriverProvider.browserName = INTERNET_EXPLORER_FULL_NAME;
    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(true));

    WebDriverRunner.closeWebDriver();
    RemoteDriverProvider.browserName = FIREFOX;

    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(false));
  }


  public static class LocalDriverProvider implements WebDriverProvider {
    private static Class<? extends WebDriver> driverClass = InternetExplorerDriver.class;

    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return mock(driverClass, RETURNS_DEEP_STUBS);
    }
  }

  public static class RemoteDriverProvider implements WebDriverProvider {
    private static String browserName = INTERNET_EXPLORER_FULL_NAME;

    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      RemoteWebDriver driver = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);
      when(driver.getCapabilities().getBrowserName()).thenReturn(browserName);
      return driver;
    }
  }

}
