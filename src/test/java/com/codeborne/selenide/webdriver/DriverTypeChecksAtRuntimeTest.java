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
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DriverTypeChecksAtRuntimeTest {

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
    WebDriver ffDriver = mock(FirefoxDriver.class, RETURNS_DEEP_STUBS);
    WebDriverRunner.setWebDriver(ffDriver);

    assertThat(isIE(), is(false));
  }

  @Test
  public void webDriverProviderReturnsInternetExplorerDriverInstanceTest() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.webdriver.DriverTypeChecksAtRuntimeTest$IeDriverProvider";

    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(true));
  }

  @Test
  public void webDriverProviderReturnsFirefoxDriverInstanceTest() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.webdriver.DriverTypeChecksAtRuntimeTest$FirefoxDriverProvider";

    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(false));
  }

  @Test
  public void webDriverProviderReturnsRemoteInternetExplorerDriverInstanceTest() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = "com.codeborne.selenide.webdriver.DriverTypeChecksAtRuntimeTest$RemoteDriverProvider";

    WebDriver driver = WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(true));

    WebDriverRunner.closeWebDriver();
    WebDriverRunner.getWebDriver();
    assertThat(isIE(), is(false));
  }


  public static class IeDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return mock(InternetExplorerDriver.class, RETURNS_DEEP_STUBS);
    }
  }

  public static class FirefoxDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return mock(FirefoxDriver.class, RETURNS_DEEP_STUBS);
    }
  }

  public static class RemoteDriverProvider implements WebDriverProvider {
    static int calls = 0;

    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      RemoteWebDriver driver = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);
      if (++calls == 1) {
        when(driver.getCapabilities().getBrowserName()).
            thenReturn("internet explorer");
      } else {
        when(driver.getCapabilities().getBrowserName()).
            thenReturn("firefox");
      }
      return driver;
    }
  }

}
