package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppiumDriverUnwrapperTest {
  private final WebDriver pureDriver = new FakeIOSDriver();
  private final WebDriver wrappedDriver = addWebDriverListeners(pureDriver, new EmptyWebDriverListener());
  private final Driver driver = mock(Driver.class);

  @BeforeEach
  void setUp() {
    when(driver.getWebDriver()).thenReturn(wrappedDriver);
    assertThat(wrappedDriver).isNotInstanceOf(IOSDriver.class);
    assertThat(driver).isNotInstanceOf(IOSDriver.class);
  }

  @Test
  void test_isMobile() {
    assertThat(isMobile(wrappedDriver)).isTrue();
    assertThat(isMobile(driver)).isTrue();
  }

  @Test
  void test_isIos() {
    assertThat(isIos(wrappedDriver)).isTrue();
    assertThat(isIos(driver)).isTrue();
  }

  @Test
  void test_isAndroid() {
    assertThat(isAndroid(wrappedDriver)).isFalse();
    assertThat(isAndroid(driver)).isFalse();
  }

  private WebDriver addWebDriverListeners(WebDriver webdriver, WebDriverListener listener) {
    EventFiringDecorator<WebDriver> wrapper = new EventFiringDecorator<>(listener);
    return wrapper.decorate(webdriver);
  }
}

class EmptyWebDriverListener implements WebDriverListener {
}

class FakeIOSDriver extends IOSDriver {
  FakeIOSDriver() {
    super(getUrl(), new XCUITestOptions());
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }

  private static URL getUrl() {
    try {
      return new URL("http://localhost:4723/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
