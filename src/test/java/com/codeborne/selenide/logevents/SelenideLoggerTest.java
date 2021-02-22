package com.codeborne.selenide.logevents;

import integration.UseLocaleExtension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static com.codeborne.selenide.logevents.SelenideLogger.readableArguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class SelenideLoggerTest implements WithAssertions {
  private final WebDriver webdriver = mock(WebDriver.class);

  @RegisterExtension
  static UseLocaleExtension useLocale = new UseLocaleExtension("en");

  @BeforeEach
  @AfterEach
  void setUp() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void convertsJavaMethodNameToHumanReadableClause() {
    assertThat(SelenideLogger.readableMethodName("click"))
      .isEqualTo("click");
    assertThat(SelenideLogger.readableMethodName("setValue"))
      .isEqualTo("set value");
    assertThat(SelenideLogger.readableMethodName("shouldBe"))
      .isEqualTo("should be");
    assertThat(SelenideLogger.readableMethodName("convertsJavaMethodNameToHumanReadableClause"))
      .isEqualTo("converts java method name to human readable clause");
  }

  @Test
  void printsReadableArgumentsValues() {
    assertThat(readableArguments((Object[]) null)).isEqualTo("");
    assertThat(readableArguments(111)).isEqualTo("111");
    assertThat(readableArguments(1, 2, 3)).isEqualTo("[1, 2, 3]");
    assertThat(readableArguments((Object[]) new String[]{"a"})).isEqualTo("a");
    assertThat(readableArguments((Object[]) new String[]{"a", "bb"})).isEqualTo("[a, bb]");
    assertThat(readableArguments((Object[]) new String[]{null})).isEqualTo("null");
    assertThat(readableArguments((Object[]) new String[]{null, "a", null})).isEqualTo("[null, a, null]");
    assertThat(readableArguments((Object) new int[]{1})).isEqualTo("1");
    assertThat(readableArguments((Object) new int[]{1, 2})).isEqualTo("[1, 2]");
  }

  @Test
  void printsDurationAmongArguments() {
    assertThat(readableArguments(Duration.ofMillis(900))).isEqualTo("900 ms.");
    assertThat(readableArguments(visible, Duration.ofSeconds(42))).isEqualTo("[visible, 42 s.]");
    assertThat(readableArguments(visible, Duration.ofMillis(8500))).isEqualTo("[visible, 8.500 s.]");
    assertThat(readableArguments(visible, Duration.ofMillis(900))).isEqualTo("[visible, 900 ms.]");
    assertThat(readableArguments(visible, Duration.ofNanos(0))).isEqualTo("[visible, 0 ms.]");
  }

  @Test
  void canAddManyListenersPerThread() {
    LogEventListener listener1 = mock(LogEventListener.class);
    LogEventListener listener2 = mock(LogEventListener.class);
    LogEventListener listener3 = mock(LogEventListener.class);

    SelenideLogger.addListener("simpleReport", listener1);
    SelenideLogger.addListener("softAsserts", listener2);
    SelenideLogger.addListener("userProvided", listener3);

    WebElement webElement = mock(WebElement.class);
    when(webdriver.findElement(By.cssSelector("div"))).thenReturn(webElement);
    when(webElement.isDisplayed()).thenReturn(true);

    SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click", null), PASS);

    verifyEvent(listener1, "div", "click()", PASS);
    verifyEvent(listener2, "div", "click()", PASS);
    verifyEvent(listener3, "div", "click()", PASS);

    verifyNoMoreInteractions(listener1, listener2, listener3);

    reset(listener1, listener2, listener3);
    SelenideLogger.removeListener("simpleReport");
    SelenideLogger.removeListener("softAsserts");

    SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click", null), PASS);
    verifyEvent(listener3, "div", "click()", PASS);

    verifyNoMoreInteractions(listener1, listener2, listener3);
  }

  @Test
  void doesNotFail_ifSomeOfListeners_before_throwsException() {
    LogEventListener listener1 = mock(LogEventListener.class);
    doThrow(new IllegalStateException("Failed to take screenshot because browser is not opened yet"))
      .when(listener1).beforeEvent(any());
    SelenideLogger.addListener("allureListener", listener1);

    SelenideLogger.commitStep(SelenideLogger.beginStep("open", "https://any.url"), FAIL);

    verifyEvent(listener1, "open", "https://any.url", FAIL);
    verifyNoMoreInteractions(listener1);
  }

  @Test
  void doesNotFail_ifSomeOfListeners_after_throwsException() {
    LogEventListener listener1 = mock(LogEventListener.class);
    doThrow(new IllegalStateException("Failed to take screenshot because browser is not opened yet"))
      .when(listener1).afterEvent(any());
    SelenideLogger.addListener("allureListener", listener1);

    SelenideLogger.commitStep(SelenideLogger.beginStep("open", "https://any.url"), FAIL);

    verifyEvent(listener1, "open", "https://any.url", FAIL);
    verifyNoMoreInteractions(listener1);
  }

  private void verifyEvent(LogEventListener listener, String element, String subject, LogEvent.EventStatus status) {
    ArgumentCaptor<LogEvent> event = ArgumentCaptor.forClass(LogEvent.class);
    verify(listener).beforeEvent(event.capture());
    verify(listener).afterEvent(event.capture());
    LogEvent value = event.getValue();
    assertThat(value.getElement()).isEqualTo(element);
    assertThat(value.getSubject()).isEqualTo(subject);
    assertThat(value.getStatus()).isEqualTo(status);
  }
}
