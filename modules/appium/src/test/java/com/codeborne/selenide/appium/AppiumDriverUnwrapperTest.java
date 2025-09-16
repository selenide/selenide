package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapperTest.add;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapperTest.getUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;

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
  void isMobile_false_forWebBrowser(String webBrowser) {
    capabilities.setCapability(BROWSER_NAME, webBrowser);

    WebDriver webDriver = new FakeWebDriver(capabilities);
    assertThat(isMobile(driver(webDriver))).isFalse();
    assertThat(isMobile(driver(webDriver).getWebDriver())).isFalse();
  }

  @Test
  void test_isIos() {
    assertThat(isIos(driver(new FakeIOSDriver(capabilities)))).isTrue();
    assertThat(isIos(driver(new FakeAndroidDriver(capabilities)))).isFalse();
    assertThat(isIos(driver(new FakeWebDriver(capabilities)))).isFalse();
  }

  @Test
  void test_isAndroid() {
    assertThat(isAndroid(driver(new FakeAndroidDriver(capabilities)))).isTrue();
    assertThat(isAndroid(driver(new FakeIOSDriver(capabilities)))).isFalse();
    assertThat(isAndroid(driver(new FakeWebDriver(capabilities)))).isFalse();
  }

  private static Stream<Arguments> webBrowsers() {
    return Stream.of(
      Arguments.of(Browser.IE.browserName()),
      Arguments.of(Browser.EDGE.browserName()),
      Arguments.of(Browser.CHROME.browserName()),
      Arguments.of(Browser.FIREFOX.browserName()),
      Arguments.of(Browser.HTMLUNIT.browserName()),
      Arguments.of(Browser.OPERA.browserName()),
      Arguments.of(Browser.SAFARI.browserName()),
      Arguments.of(Browser.SAFARI_TECH_PREVIEW.browserName())
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

class FakeIOSDriver extends IOSDriver {
  FakeIOSDriver(Capabilities capabilities) {
    super(getUrl(), new XCUITestOptions(capabilities));
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }
}

class FakeAndroidDriver extends AndroidDriver {
  FakeAndroidDriver(Capabilities capabilities) {
    super(getUrl(), new UiAutomator2Options(capabilities));
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }
}

class FakeWebDriver extends RemoteWebDriver {
  FakeWebDriver(MutableCapabilities capabilities) {
    super(getUrl(), add(capabilities, BROWSER_NAME, Browser.CHROME.browserName()));
  }

  @Override
  protected void startSession(Capabilities capabilities) {
  }
}
