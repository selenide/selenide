package com.codeborne.selenide.appium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeborne.selenide.Driver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;

class WebdriverUnwrapperTest {
  @Test
  void shouldUnwrapWebDriverWithEventListener() {
    WebDriver pureDriver = new FakeIOSDriver();
    WebDriver wrappedDriver = addEventListener(pureDriver, new EmptyWebDriverEventListener());
    Driver driver = mock(Driver.class);
    when(driver.getWebDriver()).thenReturn(wrappedDriver);
    assertThat(wrappedDriver).isNotInstanceOf(IOSDriver.class);

    assertThat(WebdriverUnwrapper.instanceOf(wrappedDriver, IOSDriver.class)).isTrue();
    assertThat(WebdriverUnwrapper.cast(wrappedDriver, IOSDriver.class).get()).isInstanceOf(IOSDriver.class);

    assertThat(WebdriverUnwrapper.instanceOf(driver, IOSDriver.class)).isTrue();
    assertThat(WebdriverUnwrapper.cast(driver, IOSDriver.class).get()).isInstanceOf(IOSDriver.class);
  }

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

  private WebDriver addEventListener(WebDriver webdriver, WebDriverEventListener eventListener) {
    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    wrapper.register(eventListener);
    return wrapper;
  }

  private WebDriver addWebDriverListeners(WebDriver webdriver, WebDriverListener listener) {
    EventFiringDecorator<WebDriver> wrapper = new EventFiringDecorator<>(listener);
    return wrapper.decorate(webdriver);
  }
}

class EmptyWebDriverEventListener implements WebDriverEventListener {

  @Override
  public void beforeAlertAccept(WebDriver driver) {

  }

  @Override
  public void afterAlertAccept(WebDriver driver) {

  }

  @Override
  public void afterAlertDismiss(WebDriver driver) {

  }

  @Override
  public void beforeAlertDismiss(WebDriver driver) {

  }

  @Override
  public void beforeNavigateTo(String url, WebDriver driver) {

  }

  @Override
  public void afterNavigateTo(String url, WebDriver driver) {

  }

  @Override
  public void beforeNavigateBack(WebDriver driver) {

  }

  @Override
  public void afterNavigateBack(WebDriver driver) {

  }

  @Override
  public void beforeNavigateForward(WebDriver driver) {

  }

  @Override
  public void afterNavigateForward(WebDriver driver) {

  }

  @Override
  public void beforeNavigateRefresh(WebDriver driver) {

  }

  @Override
  public void afterNavigateRefresh(WebDriver driver) {

  }

  @Override
  public void beforeFindBy(By by, WebElement element, WebDriver driver) {

  }

  @Override
  public void afterFindBy(By by, WebElement element, WebDriver driver) {

  }

  @Override
  public void beforeClickOn(WebElement element, WebDriver driver) {

  }

  @Override
  public void afterClickOn(WebElement element, WebDriver driver) {

  }

  @Override
  public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {

  }

  @Override
  public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {

  }

  @Override
  public void beforeScript(String script, WebDriver driver) {

  }

  @Override
  public void afterScript(String script, WebDriver driver) {

  }

  @Override
  public void beforeSwitchToWindow(String windowName, WebDriver driver) {

  }

  @Override
  public void afterSwitchToWindow(String windowName, WebDriver driver) {

  }

  @Override
  public void onException(Throwable throwable, WebDriver driver) {

  }

  @Override
  public <X> void beforeGetScreenshotAs(OutputType<X> target) {

  }

  @Override
  public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {

  }

  @Override
  public void beforeGetText(WebElement element, WebDriver driver) {

  }

  @Override
  public void afterGetText(WebElement element, WebDriver driver, String text) {

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
