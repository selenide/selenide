package com.codeborne.selenide.logevents;

import com.codeborne.selenide.WebDriverRunner;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SelenideLoggerTest {

  private WebDriver webdriver = mock(WebDriver.class);

  @BeforeEach
  void setUp() {
    WebDriverRunner.closeWebDriver();
    WebDriverRunner.setWebDriver(webdriver);
  }

  @AfterEach
  void tearDown() {
    WebDriverRunner.closeWebDriver();
  }

  @Test
  void convertsJavaMethodNameToHumanReadableClause() {
    Assertions.assertEquals("click", SelenideLogger.readableMethodName("click"));
    Assertions.assertEquals("set value", SelenideLogger.readableMethodName("setValue"));
    Assertions.assertEquals("should be", SelenideLogger.readableMethodName("shouldBe"));
    Assertions.assertEquals("converts java method name to human readable clause",
      SelenideLogger.readableMethodName("convertsJavaMethodNameToHumanReadableClause"));
  }

  @Test
  void printsReadableArgumentsValues() {
    Assertions.assertEquals("", SelenideLogger.readableArguments((Object[]) null));
    Assertions.assertEquals("111", SelenideLogger.readableArguments(111));
    Assertions.assertEquals("[1, 2, 3]", SelenideLogger.readableArguments(1, 2, 3));
    Assertions.assertEquals("a", SelenideLogger.readableArguments((Object[]) new String[]{"a"}));
    Assertions.assertEquals("[a, bb]", SelenideLogger.readableArguments((Object[]) new String[]{"a", "bb"}));
    Assertions.assertEquals("null", SelenideLogger.readableArguments((Object[]) new String[]{null}));
    Assertions.assertEquals("[null, a, null]", SelenideLogger.readableArguments((Object[]) new String[]{null, "a", null}));
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

    $("div").click();

    verifyEvent(listener1);
    verifyEvent(listener2);
    verifyEvent(listener3);

    verifyNoMoreInteractions(listener1, listener2, listener3);

    reset(listener1, listener2, listener3);
    SelenideLogger.removeListener("simpleReport");
    SelenideLogger.removeListener("softAsserts");

    $("div").click();
    verifyEvent(listener3);

    verifyNoMoreInteractions(listener1, listener2, listener3);
  }

  private void verifyEvent(LogEventListener listener1) {
    ArgumentCaptor<LogEvent> event = ArgumentCaptor.forClass(LogEvent.class);
    verify(listener1).onEvent(event.capture());
    LogEvent value = event.getValue();
    MatcherAssert.assertThat(value.getElement(), equalTo("div"));
    MatcherAssert.assertThat(value.getSubject(), equalTo("click()"));
    MatcherAssert.assertThat(value.getStatus(), equalTo(PASS));
  }
}
