package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static org.assertj.core.api.Assertions.assertThat;

class AppiumDriverUnwrapperTest {
  private final MutableCapabilities capabilities = new MutableCapabilities();

  @Test
  void isMobile_ios() {
    WebDriver webDriver = new FakeIOSDriver(capabilities);
    assertThat(isMobile(driver(webDriver))).isTrue();
    assertThat(isMobile(driver(webDriver).getWebDriver())).isTrue();
  }

  @Test
  void isMobile_android() {
    WebDriver webDriver = new FakeAndroidDriver(capabilities);
    assertThat(isMobile(driver(webDriver))).isTrue();
    assertThat(isMobile(driver(webDriver).getWebDriver())).isTrue();
  }

  @ParameterizedTest
  @MethodSource("webBrowsers")
  void isMobile_false_forWebBrowser(Browser webBrowser) {
    WebDriver webDriver = new FakeWebDriver(capabilities, webBrowser);
    assertThat(isMobile(driver(webDriver))).isFalse();
    assertThat(isMobile(driver(webDriver).getWebDriver())).isFalse();
  }

  @Test
  void test_isIos() {
    assertThat(isIos(driver(new FakeIOSDriver(capabilities)))).isTrue();
    assertThat(isIos(driver(new FakeAndroidDriver(capabilities)))).isFalse();
    assertThat(isIos(driver(new FakeWebDriver(capabilities, Browser.CHROME)))).isFalse();
  }

  @Test
  void test_isAndroid() {
    assertThat(isAndroid(driver(new FakeAndroidDriver(capabilities)))).isTrue();
    assertThat(isAndroid(driver(new FakeIOSDriver(capabilities)))).isFalse();
    assertThat(isAndroid(driver(new FakeWebDriver(capabilities, Browser.CHROME)))).isFalse();
  }

  static Stream<Arguments> webBrowsers() {
    return Stream.of(
      Arguments.of(Browser.IE),
      Arguments.of(Browser.EDGE),
      Arguments.of(Browser.CHROME),
      Arguments.of(Browser.FIREFOX),
      Arguments.of(Browser.HTMLUNIT),
      Arguments.of(Browser.OPERA),
      Arguments.of(Browser.SAFARI),
      Arguments.of(Browser.SAFARI_TECH_PREVIEW)
    );
  }

  private WebDriver addWebDriverListeners(WebDriver webdriver, WebDriverListener listener) {
    EventFiringDecorator<WebDriver> wrapper = new EventFiringDecorator<>(listener);
    return wrapper.decorate(webdriver);
  }

  private Driver driver(WebDriver webDriver) {
    WebDriver wrappedDriver = addWebDriverListeners(webDriver, new EmptyWebDriverListener());
    assertThat(wrappedDriver).isNotInstanceOf(IOSDriver.class);
    return new DriverStub(wrappedDriver);
  }

  static URL getUrl() {
    try {
      return new URL("http://localhost:4723/");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  static MutableCapabilities add(MutableCapabilities capabilities, String name, String value) {
    capabilities.setCapability(name, value);
    return capabilities;
  }
}

class EmptyWebDriverListener implements WebDriverListener {
}
