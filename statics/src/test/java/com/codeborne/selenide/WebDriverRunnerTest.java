package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebDriverThreadLocalContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverListener;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.net.URL;

import static com.codeborne.selenide.Browsers.FIREFOX;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.TestResources.toURL;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

final class WebDriverRunnerTest {
  private static WebDriver driver;

  private final URL url = toURL("start_page.html");

  @BeforeEach
  void resetWebDriverContainer() {
    driver = mock(RemoteWebDriver.class, RETURNS_DEEP_STUBS);
    doReturn(mock(Navigation.class)).when(driver).navigate();

    WebDriverRunner.webdriverContainer = spy(new WebDriverThreadLocalContainer());
    doReturn(null).when((JavascriptExecutor) driver).executeScript(anyString(), any());
    Configuration.proxyEnabled = false;
    Configuration.fileDownload = HTTPGET;
  }

  @AfterEach
  void resetSettings() {
    WebDriverRunner.closeWebDriver();
    driver = null;
    Configuration.browser = System.getProperty("browser", FIREFOX);
    webdriverContainer = new WebDriverThreadLocalContainer();
  }

  @Test
  void allowsToSpecifyCustomWebDriverConfiguration() {
    WebDriverRunner.closeWebDriver();
    Configuration.browser = CustomWebDriverProvider.class.getName();

    assertThat(WebDriverRunner.getAndCheckWebDriver())
      .isEqualTo(driver);
  }

  @Test
  void allowsToSpecifyCustomWebDriverProgrammatically() {
    RemoteWebDriver myDriver = mock();
    doReturn(mock(Navigation.class)).when(myDriver).navigate();
    WebDriverRunner.setWebDriver(myDriver);

    open(url);
    assertThat(WebDriverRunner.getWebDriver())
      .isEqualTo(myDriver);
  }

  @Test
  void userCanAddWebDriverListeners() {
    WebDriverListener listener = mock();
    WebDriverRunner.addListener(listener);
    Configuration.browser = CustomWebDriverProvider.class.getName();
    open(url);
    verify(listener).beforeAnyNavigationCall(any(Navigation.class), any(), any());
  }

  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
      return driver;
    }
  }
}
