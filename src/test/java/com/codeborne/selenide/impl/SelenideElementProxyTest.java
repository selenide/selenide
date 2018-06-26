package com.codeborne.selenide.impl;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.present;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.impl.SelenideElementProxy.shouldRetryAfterError;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementProxyTest {
  private static final Logger log = Logger.getLogger(SelenideElementProxyTest.class.getName());

  private RemoteWebDriver webdriver = mock(RemoteWebDriver.class);
  private WebElement element = mock(WebElement.class);

  @AfterAll
  static void restoreOldValues() {
    Configuration.screenshots = true;
  }

  @BeforeEach
  void mockWebDriver() {
    Configuration.timeout = 3;
    Configuration.pollingInterval = 1;
    Configuration.screenshots = false;

    WebDriverRunner.setWebDriver(webdriver);
    when(webdriver
      .executeScript(anyString(), any(WebElement.class)))
      .thenReturn(ImmutableMap.of("id", "id1", "class", "class1"));

    Screenshots.screenshots = mock(ScreenShotLaboratory.class);
    when(Screenshots.screenshots.takeScreenShot()).thenReturn("");

    when(element.getTagName()).thenReturn("h1");
    when(element.getText()).thenReturn("Hello world");
    when(element.isDisplayed()).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    WebDriverRunner.closeWebDriver();
  }

  @AfterEach
  void after() {
    SelenideLogger.removeListener("test");
  }

  @Test
  void elementShouldBeVisible() {
    when(element.isDisplayed()).thenReturn(true);
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    $("#firstName").shouldBe(visible);
  }

  @Test
  void elementNotFound() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(null);
    Assertions.assertThrows(ElementNotFound.class, () -> $("#firstName").shouldBe(visible));
  }

  @Test
  void elementFoundButNotMatched() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenReturn(false);
    Assertions.assertThrows(ElementShould.class, () -> $("#firstName").shouldBe(visible));
  }

  @Test
  void elementFoundButInvisible() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenThrow(new WebDriverException("failed to call isDisplayed"));
    Assertions.assertThrows(ElementShould.class, () -> $("#firstName").shouldBe(visible));
  }

  @Test
  void elementFoundButConditionCheckFailed() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.isDisplayed()).thenReturn(true);
    Assertions.assertThrows(ElementShould.class, () -> $("#firstName").shouldHave(text("goodbye")));
  }

  @Test
  void elementNotFoundAsExpected() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(null);
    $("#firstName").shouldNotBe(exist);
    $("#firstName").shouldNotBe(present);
    $("#firstName").should(disappear);
    $("#firstName").shouldNotBe(visible);
    $("#firstName").shouldNotBe(enabled);
    $("#firstName").shouldNotHave(text("goodbye"));
  }

  @Test
  void elementNotFoundAsExpected2() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
      .thenThrow(new WebDriverException("element is not found and this is expected"));
    $("#firstName").shouldNot(exist);
    $("#firstName").shouldNotBe(present);
    $("#firstName").should(disappear);
    $("#firstName").shouldNotBe(visible);
    $("#firstName").shouldNotBe(enabled);
    $("#firstName").shouldNotHave(text("goodbye"));
  }

  @Test
  void webdriverReportsInvalidXpath_using_should() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
      .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    Assertions.assertThrows(InvalidSelectorException.class, () -> $("#firstName").should(disappear));
  }

  @Test
  void webdriverReportsInvalidXpath_using_shouldNot() {
    when(webdriver.findElement(By.cssSelector("#firstName")))
      .thenThrow(new InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"));
    Assertions.assertThrows(InvalidSelectorException.class, () -> $("#firstName").shouldNot(exist));
  }

  @Test
  void setValueShouldNotFailIfElementHasDisappearedWhileEnteringText() {
    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(webdriver.executeScript(anyString(), any()))
      .thenThrow(new StaleElementReferenceException("element disappeared after entering text"));
    $("#firstName").setValue("john");
  }

  @Test
  void shouldLogSetValueSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "set value", PASS));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    SelenideElement selEl = $("#firstName");
    selEl.setValue("ABC");
  }

  private LogEventListener createListener(final String selector, final String subject,
                                          final EventStatus status) {
    return currentLog -> {
      String format = String.format("{%s} %s: %s", currentLog.getElement(), currentLog.getSubject(), currentLog.getStatus());
      log.info(format);
      MatcherAssert.assertThat(currentLog.getElement(), containsString(selector));
      MatcherAssert.assertThat(currentLog.getSubject(), containsString(subject));
      Assertions.assertEquals(currentLog.getStatus(), status);
    };
  }

  @Test
  void shouldLogShouldSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "should have", PASS));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("ABC");
    SelenideElement selEl = $("#firstName");
    selEl.shouldHave(value("ABC"));
  }

  @Test
  void shouldLogShouldNotSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "should not have", PASS));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");
    SelenideElement selEl = $("#firstName");
    selEl.shouldNotHave(value("ABC"));
  }

  @Test
  void shouldLogFailedShouldNotSubject() {
    String selector = "#firstName";
    SelenideLogger.addListener("test", createListener(selector, "should have", FAIL));

    when(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element);
    when(element.getAttribute("value")).thenReturn("wrong value");

    Assertions.assertThrows(ElementShould.class, () -> $("#firstName").shouldHave(value("ABC")));
  }

  @Test
  void shouldNotRetry_onIllegalArgumentException() {
    MatcherAssert.assertThat(shouldRetryAfterError(new IllegalArgumentException("The element does not have href attribute")),
      is(false));
  }

  @Test
  void shouldNotRetry_onFileNotFoundException() {
    MatcherAssert.assertThat(shouldRetryAfterError(new FileNotFoundException("bla")), is(false));
  }

  @Test
  void shouldNotRetry_onClassLoadingException() {
    MatcherAssert.assertThat(shouldRetryAfterError(new ClassNotFoundException("bla")), is(false));
  }

  @Test
  void shouldNotRetry_onClassDefLoadingException() {
    MatcherAssert.assertThat(shouldRetryAfterError(new NoClassDefFoundError("bla")), is(false));
  }

  @Test
  void shouldRetry_onAssertionError() {
    MatcherAssert.assertThat(shouldRetryAfterError(new AssertionError("bla")), is(true));
  }

  @Test
  void shouldRetry_onAnyOtherException() {
    MatcherAssert.assertThat(shouldRetryAfterError(new Exception("bla")), is(true));
  }
}
