package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebdriverUnwrapperTest {
  @Test
  void shouldUnwrapWebDriverWithDriverListener() {
    WebDriver pureDriver = new FakeIOSDriver();
    WebDriver wrappedDriver = addWebDriverListeners(pureDriver, new EmptyWebDriverListener());
    Driver driver = mock(Driver.class);
    when(driver.getWebDriver()).thenReturn(wrappedDriver);
    assertThat(wrappedDriver).isNotInstanceOf(IOSDriver.class);

    assertThat(WebdriverUnwrapper.instanceOf(wrappedDriver, IOSDriver.class)).isTrue();
    assertThat(WebdriverUnwrapper.cast(wrappedDriver, IOSDriver.class).get()).isInstanceOf(IOSDriver.class);

    assertThat(WebdriverUnwrapper.instanceOf(driver, IOSDriver.class)).isTrue();
    assertThat(WebdriverUnwrapper.cast(driver, IOSDriver.class).get()).isInstanceOf(IOSDriver.class);
  }

  @Test
  void shouldHandleIncompatibleDriverTypes() {
    WebDriver pureDriver = new FakeIOSDriver();
    assertThat(WebdriverUnwrapper.instanceOf(pureDriver, AndroidDriver.class)).isFalse();
    assertThat(WebdriverUnwrapper.cast(pureDriver, AndroidDriver.class)).isEmpty();
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
