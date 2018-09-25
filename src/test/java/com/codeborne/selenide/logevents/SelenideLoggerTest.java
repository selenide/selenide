package com.codeborne.selenide.logevents;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SelenideLoggerTest implements WithAssertions {
  private WebDriver webdriver = mock(WebDriver.class);

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

    verifyEvent(listener1);
    verifyEvent(listener2);
    verifyEvent(listener3);

    verifyNoMoreInteractions(listener1, listener2, listener3);

    reset(listener1, listener2, listener3);
    SelenideLogger.removeListener("simpleReport");
    SelenideLogger.removeListener("softAsserts");

    SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click", null), PASS);
    verifyEvent(listener3);

    verifyNoMoreInteractions(listener1, listener2, listener3);
  }

  private void verifyEvent(LogEventListener listener1) {
    ArgumentCaptor<LogEvent> event = ArgumentCaptor.forClass(LogEvent.class);
    verify(listener1).onEvent(event.capture());
    LogEvent value = event.getValue();
    assertThat(value.getElement()).isEqualTo("div");
    assertThat(value.getSubject()).isEqualTo("click()");
    assertThat(value.getStatus()).isEqualTo(PASS);
  }
}
