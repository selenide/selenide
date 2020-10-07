package com.codeborne.selenide.logevents;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class SelenideLoggerTest implements WithAssertions {
  private final WebDriver webdriver = mock(WebDriver.class);

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
    assertThat(SelenideLogger.readableArguments((Object[]) null))
      .isEqualTo("");
    assertThat(SelenideLogger.readableArguments(111))
      .isEqualTo("111");
    assertThat(SelenideLogger.readableArguments(1, 2, 3))
      .isEqualTo("[1, 2, 3]");
    assertThat(SelenideLogger.readableArguments((Object[]) new String[]{"a"}))
      .isEqualTo("a");
    assertThat(SelenideLogger.readableArguments((Object[]) new String[]{"a", "bb"}))
      .isEqualTo("[a, bb]");
    assertThat(SelenideLogger.readableArguments((Object[]) new String[]{null}))
      .isEqualTo("null");
    assertThat(SelenideLogger.readableArguments((Object[]) new String[]{null, "a", null}))
      .isEqualTo("[null, a, null]");
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
