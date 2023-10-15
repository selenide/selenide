package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SharedDownloadsFolder;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.impl.SelenideElementProxy.isSelenideElementMethod;
import static com.codeborne.selenide.impl.SelenideElementProxy.shouldRetryAfterError;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class SelenideElementProxyTest {
  private static final Logger log = LoggerFactory.getLogger(SelenideElementProxyTest.class);

  private final RemoteWebDriver webdriver = mock();
  private final WebElement element = mock();
  private final SelenideConfig config = new SelenideConfig().screenshots(false).timeout(1);
  private final SelenideDriver driver = new SelenideDriver(config, webdriver, null, new SharedDownloadsFolder("build/downloads/123"));

  @BeforeEach
  void mockWebDriver() {
    when(webdriver.executeScript(anyString(), same(element)))
      .thenReturn(ImmutableMap.of("id", "id1", "class", "class1"));
    when(webdriver.getPageSource()).thenReturn("<html>mock</html>");
    when(webdriver.executeScript("return navigator.platform")).thenReturn("Win32");
    when(element.getTagName()).thenReturn("h1");
    when(element.getText()).thenReturn("Hello world");
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
  }

  @AfterEach
  void after() {
    SelenideLogger.removeListener("test");
  }

  @Test
  void elementShouldBeVisible() {
    when(element.isDisplayed()).thenReturn(true);
    when(webdriver.findElement(any())).thenReturn(element);
    driver.find("#firstName").shouldBe(visible);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFound() {
    when(webdriver.findElement(any())).thenThrow(new NotFoundException());
    assertThatThrownBy(() -> driver.find("#firstName").shouldBe(visible)).isInstanceOf(ElementNotFound.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementFoundButNotMatched() {
    when(webdriver.findElement(any())).thenReturn(element);
    when(element.isDisplayed()).thenReturn(false);
    assertThatThrownBy(() -> driver.find("#firstName").shouldBe(visible)).isInstanceOf(ElementShould.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementFoundButInvisible() {
    when(webdriver.findElement(any())).thenReturn(element);
    when(element.isDisplayed()).thenThrow(new WebDriverException("failed to call isDisplayed"));
    assertThatThrownBy(() -> driver.find("#firstName").shouldBe(visible)).isInstanceOf(ElementShould.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementFoundButConditionCheckFailed() {
    when(webdriver.findElement(any())).thenReturn(element);
    when(element.isDisplayed()).thenReturn(true);
    assertThatThrownBy(() -> driver.find("#firstName").shouldHave(text("goodbye"))).isInstanceOf(ElementShould.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected_case1() {
    when(webdriver.findElement(any())).thenThrow(new NotFoundException());
    driver.find("#firstName").shouldNotBe(exist);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected_case2() {
    when(webdriver.findElement(any())).thenThrow(new NotFoundException());
    driver.find("#firstName").should(disappear);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected_case3() {
    when(webdriver.findElement(any())).thenThrow(new NotFoundException());
    driver.find("#firstName").shouldNotBe(visible);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected_case4() {
    when(webdriver.findElement(any())).thenThrow(new NotFoundException());
    assertThatThrownBy(() -> driver.find("#firstName").shouldNotBe(enabled)).isInstanceOf(ElementNotFound.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected_case5() {
    when(webdriver.findElement(any())).thenThrow(new NotFoundException());
    assertThatThrownBy(() -> driver.find("#firstName").shouldNotHave(text("goodbye"))).isInstanceOf(ElementNotFound.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected2_case1() {
    when(webdriver.findElement(any())).thenThrow(new WebDriverException("element is not found and this is expected"));
    driver.find("#firstName").shouldNot(exist);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected2_case2() {
    when(webdriver.findElement(any())).thenThrow(new WebDriverException("element is not found and this is expected"));
    driver.find("#firstName").should(disappear);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected2_case3() {
    when(webdriver.findElement(any())).thenThrow(new WebDriverException("element is not found and this is expected"));
    driver.find("#firstName").shouldNotBe(visible);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected2_case4() {
    when(webdriver.findElement(any())).thenThrow(new WebDriverException("element is not found and this is expected"));
    assertThatThrownBy(() -> driver.find("#firstName").shouldNotBe(enabled)).isInstanceOf(ElementNotFound.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void elementNotFoundAsExpected2_case5() {
    when(webdriver.findElement(any())).thenThrow(new WebDriverException("element is not found and this is expected"));
    assertThatThrownBy(() -> driver.find("#firstName").shouldNotHave(text("goodbye"))).isInstanceOf(ElementNotFound.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void webdriverReportsInvalidXpath_using_should() {
    when(webdriver.findElement(any()))
      .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    assertThatThrownBy(() -> driver.find("#firstName").should(disappear))
      .isInstanceOf(InvalidSelectorException.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void webdriverReportsInvalidXpath_using_shouldNot() {
    when(webdriver.findElement(any()))
      .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    assertThatThrownBy(() -> driver.find("#firstName").shouldNot(exist))
      .isInstanceOf(InvalidSelectorException.class);
    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @Test
  void shouldLogSetValueSubject() {
    SelenideLogger.addListener("test", new TestEventListener("#firstName", "set value", PASS));
    when(webdriver.findElement(any())).thenReturn(element);

    driver.find("#firstName").setValue("ABC");

    verify(webdriver).findElement(By.cssSelector("#firstName"));
  }

  @ParametersAreNonnullByDefault
  private static class TestEventListener implements LogEventListener {

    private final String expectSelector;
    private final String expectSubject;
    private final EventStatus expectStatus;

    TestEventListener(final String selector, final String subject, final EventStatus status) {
      this.expectSelector = selector;
      this.expectSubject = subject;
      this.expectStatus = status;
    }

    @Override
    public void afterEvent(LogEvent currentLog) {
      String format = String.format("{%s} %s: %s", currentLog.getElement(), currentLog.getSubject(), currentLog.getStatus());
      log.info(format);
      assertThat(currentLog.getElement()).contains(expectSelector);
      assertThat(currentLog.getSubject()).contains(expectSubject);
      assertThat(currentLog.getStatus()).isEqualTo(expectStatus);
    }

    @Override
    public void beforeEvent(LogEvent currentLog) {
    }
  }

  @Test
  void shouldLogShouldSubject() {
    when(webdriver.findElement(any())).thenReturn(element);
    when(element.getAttribute(any())).thenReturn("ABC");

    SelenideLogger.addListener("test", new TestEventListener("#firstName", "should have", PASS));

    driver.find("#firstName").shouldHave(value("ABC"));

    verify(webdriver).findElement(By.cssSelector("#firstName"));
    verify(element).getAttribute("value");
  }

  @Test
  void shouldLogShouldNotSubject() {
    SelenideLogger.addListener("test", new TestEventListener("#firstName", "should not have", PASS));
    when(webdriver.findElement(any())).thenReturn(element);
    when(element.getAttribute(any())).thenReturn("wrong value");

    driver.find("#firstName").shouldNotHave(value("ABC"));

    verify(webdriver).findElement(By.cssSelector("#firstName"));
    verify(element).getAttribute("value");
  }

  @Test
  void shouldLogFailedShouldNotSubject() {
    SelenideLogger.addListener("test", new TestEventListener("#firstName", "should have", FAIL));
    when(webdriver.findElement(any())).thenReturn(element);
    when(element.getAttribute(any())).thenReturn("wrong value");

    assertThatThrownBy(() -> driver.find("#firstName").shouldHave(value("ABC"))).isInstanceOf(ElementShould.class);

    verify(webdriver).findElement(By.cssSelector("#firstName"));
    verify(element, times(1)).getAttribute("value");
    verify(element, never()).getAttribute("type");
  }

  @Test
  void shouldNotRetry_onIllegalArgumentException() {
    IllegalArgumentException exception = new IllegalArgumentException("The element does not have href attribute");
    assertThat(shouldRetryAfterError(exception)).isFalse();
  }

  @Test
  void shouldNotRetry_onFileNotFoundException() {
    FileNotFoundException exception = new FileNotFoundException("bla");
    assertThat(shouldRetryAfterError(exception)).isFalse();
  }

  @Test
  void shouldNotRetry_onClassLoadingException() {
    ClassNotFoundException exception = new ClassNotFoundException("bla");
    assertThat(shouldRetryAfterError(exception)).isFalse();
  }

  @Test
  void shouldNotRetry_onClassDefLoadingException() {
    NoClassDefFoundError error = new NoClassDefFoundError("bla");
    assertThat(shouldRetryAfterError(error)).isFalse();
  }

  @Test
  void shouldNotRetry_onJavaScriptException() {
    JavascriptException exception = new JavascriptException("bla");
    assertThat(shouldRetryAfterError(exception)).isFalse();
  }

  @Test
  void shouldNotRetry_onUnhandledAlertException() {
    UnhandledAlertException exception = new UnhandledAlertException("unexpected alert open: {Alert text : Are you sure, Greg?}");
    assertThat(shouldRetryAfterError(exception)).isFalse();
  }

  @Test
  void shouldRetry_onAssertionError() {
    AssertionError error = new AssertionError("bla");
    assertThat(shouldRetryAfterError(error)).isTrue();
  }

  @Test
  void shouldRetry_onAnyOtherException() {
    Exception exception = new Exception("bla");
    assertThat(shouldRetryAfterError(exception)).isTrue();
  }

  @Test
  void detectsIfMethodsBelongsToWebElementOrSelenideElement() throws NoSuchMethodException {
    assertThat(isSelenideElementMethod(SelenideElement.class.getMethod("click"))).isTrue();
    assertThat(isSelenideElementMethod(SelenideElement.class.getMethod("findAll", String.class))).isTrue();

    assertThat(isSelenideElementMethod(WebElement.class.getMethod("click"))).isFalse();
    assertThat(isSelenideElementMethod(WebElement.class.getMethod("findElements", By.class))).isFalse();
  }

  @Test
  void supportsSubclassesOfSelenideElement() throws NoSuchMethodException {
    assertThat(isSelenideElementMethod(WebElement.class.getMethod("sendKeys", CharSequence[].class))).isFalse();
    assertThat(isSelenideElementMethod(SelenideElement.class.getMethod("click"))).isTrue();
    assertThat(isSelenideElementMethod(SelenideChildElement.class.getMethod("hook"))).isTrue();
  }

  @ParametersAreNonnullByDefault
  private interface SelenideChildElement extends SelenideElement {
    SelenideChildElement hook();
  }
}
