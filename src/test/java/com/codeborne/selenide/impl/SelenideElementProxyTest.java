package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.impl.SelenideElementProxy.shouldRetryAfterError;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementProxyTest implements WithAssertions {
  private static final Logger log = Logger.getLogger(SelenideElementProxyTest.class.getName());

  private RemoteWebDriver webdriver = mock(RemoteWebDriver.class);
  private WebElement element = mock(WebElement.class);
  private SelenideConfig config = new SelenideConfig()
    .screenshots(false)
    .timeout(3)
    .pollingInterval(1);
  private SelenideDriver driver = new SelenideDriver(config, webdriver, null);

  @BeforeEach
  void mockWebDriver() {
    when(webdriver
      .executeScript(anyString(), any(WebElement.class)))
      .thenReturn(ImmutableMap.of("id", "id1", "class", "class1"));

    when(element.getTagName()).thenReturn("h1");
    when(element.getText()).thenReturn("Hello world");
    when(element.isDisplayed()).thenReturn(true);
  }

  @AfterEach
  void after() {
    SelenideLogger.removeListener("test");
  }

  @Test
  void elementShouldBeVisible() {
    when(element.isDisplayed()).thenReturn(true);
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    driver.find("#firstName").shouldBe(visible);
  }

  @Test
  void elementNotFound() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(null);
    assertThatThrownBy(() -> driver.find("#firstName").shouldBe(visible))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void elementFoundButNotMatched() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenReturn(false);
    assertThatThrownBy(() -> driver.find("#firstName").shouldBe(visible))
      .isInstanceOf(ElementShould.class);
  }

  @Test
  void elementFoundButInvisible() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenThrow(new WebDriverException("failed to call isDisplayed"));
    assertThatThrownBy(() -> driver.find("#firstName").shouldBe(visible))
      .isInstanceOf(ElementShould.class);
  }

  @Test
  void elementFoundButConditionCheckFailed() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenReturn(true);
    assertThatThrownBy(() -> driver.find("#firstName").shouldHave(text("goodbye")))
      .isInstanceOf(ElementShould.class);
  }

  @Test
  void elementNotFoundAsExpected() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(null);
    driver.find("#firstName").shouldNotBe(exist);
    driver.find("#firstName").should(disappear);
    driver.find("#firstName").shouldNotBe(visible);
    driver.find("#firstName").shouldNotBe(enabled);
    driver.find("#firstName").shouldNotHave(text("goodbye"));
  }

  @Test
  void elementNotFoundAsExpected2() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
      .thenThrow(new WebDriverException("element is not found and this is expected"));
    driver.find("#firstName").shouldNot(exist);
    driver.find("#firstName").should(disappear);
    driver.find("#firstName").shouldNotBe(visible);
    driver.find("#firstName").shouldNotBe(enabled);
    driver.find("#firstName").shouldNotHave(text("goodbye"));
  }

  @Test
  void webdriverReportsInvalidXpath_using_should() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
      .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    assertThatThrownBy(() -> driver.find("#firstName").should(disappear))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void webdriverReportsInvalidXpath_using_shouldNot() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
      .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    assertThatThrownBy(() -> driver.find("#firstName").shouldNot(exist))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void setValueShouldNotFailIfElementHasDisappearedWhileEnteringText() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(webdriver.executeScript(anyString(), any()))
      .thenThrow(new StaleElementReferenceException("element disappeared after entering text"));
    driver.find("#firstName").setValue("john");
  }

  @Test
  void shouldLogSetValueSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "set value", PASS));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    SelenideElement selEl = driver.find("#firstName");
    selEl.setValue("ABC");
  }

  private LogEventListener createListener(final String selector, final String subject, final EventStatus status) {
    return currentLog -> {
      String format = String.format("{%s} %s: %s", currentLog.getElement(), currentLog.getSubject(), currentLog.getStatus());
      log.info(format);
      assertThat(currentLog.getElement())
        .contains(selector);
      assertThat(currentLog.getSubject())
        .contains(subject);
      assertThat(currentLog.getStatus())
        .isEqualTo(status);
    };
  }

  @Test
  void shouldLogShouldSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "should have", PASS));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("ABC");
    SelenideElement selEl = driver.find("#firstName");
    selEl.shouldHave(value("ABC"));
  }

  @Test
  void shouldLogShouldNotSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "should not have", PASS));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");
    SelenideElement selEl = driver.find("#firstName");
    selEl.shouldNotHave(value("ABC"));
  }

  @Test
  void shouldLogFailedShouldNotSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "should have", FAIL));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");

    assertThatThrownBy(() -> driver.find("#firstName").shouldHave(value("ABC")))
      .isInstanceOf(ElementShould.class);
  }

  @Test
  void shouldNotRetry_onIllegalArgumentException() {
    assertThat(shouldRetryAfterError(new IllegalArgumentException("The element does not have href attribute")))
      .isFalse();
  }

  @Test
  void shouldNotRetry_onFileNotFoundException() {
    assertThat(shouldRetryAfterError(new FileNotFoundException("bla")))
      .isFalse();
  }

  @Test
  void shouldNotRetry_onClassLoadingException() {
    assertThat(shouldRetryAfterError(new ClassNotFoundException("bla")))
      .isFalse();
  }

  @Test
  void shouldNotRetry_onClassDefLoadingException() {
    assertThat(shouldRetryAfterError(new NoClassDefFoundError("bla")))
      .isFalse();
  }

  @Test
  void shouldNotRetry_onJavaScriptException() {
    assertThat(shouldRetryAfterError(new JavascriptException("bla")))
      .isFalse();
  }

  @Test
  void shouldRetry_onAssertionError() {
    assertThat(shouldRetryAfterError(new AssertionError("bla")))
      .isTrue();
  }

  @Test
  void shouldRetry_onAnyOtherException() {
    assertThat(shouldRetryAfterError(new Exception("bla")))
      .isTrue();
  }
}
