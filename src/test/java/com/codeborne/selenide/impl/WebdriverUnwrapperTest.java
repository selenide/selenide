package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.instanceOf;
import static org.assertj.core.api.Assertions.assertThat;

class WebdriverUnwrapperTest {
  private final WebDriver pureDriver = new FakeFirefoxDriver();
  private final WebDriver wrappedDriver = addWebDriverListeners(pureDriver, new EmptyWebDriverListener());
  private final Driver driver = new DriverStub(wrappedDriver);

  @BeforeEach
  void setUp() {
    assertThat(wrappedDriver).isNotInstanceOf(FirefoxDriver.class);
  }

  @Test
  @SuppressWarnings({"removal"})
  void test_instanceOf() {
    assertThat(instanceOf(wrappedDriver, FirefoxDriver.class)).isTrue();
    assertThat(instanceOf(driver, FirefoxDriver.class)).isTrue();
    assertThat(instanceOf(pureDriver, ChromeDriver.class)).isFalse();
  }

  @Test
  @SuppressWarnings({"removal"})
  void test_cast() {
    assertThat(cast(wrappedDriver, FirefoxDriver.class).get()).isInstanceOf(FirefoxDriver.class);
    assertThat(cast(driver, FirefoxDriver.class).get()).isInstanceOf(FirefoxDriver.class);
    assertThat(cast(pureDriver, ChromeDriver.class)).isEmpty();
  }

  private WebDriver addWebDriverListeners(WebDriver webdriver, WebDriverListener listener) {
    EventFiringDecorator<WebDriver> wrapper = new EventFiringDecorator<>(listener);
    return wrapper.decorate(webdriver);
  }
}

class EmptyWebDriverListener implements WebDriverListener {
}

class FakeFirefoxDriver extends FirefoxDriver {
  @Override
  protected void startSession(Capabilities capabilities) {
  }
}
