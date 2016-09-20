package com.codeborne.selenide.logevents;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SelenideLoggerTest {

  private WebDriver webdriver = mock(WebDriver.class);

  @Before
  public void setUp() {
    WebDriverRunner.closeWebDriver();
    WebDriverRunner.setWebDriver(webdriver);
  }

  @After
  public void tearDown() {
    WebDriverRunner.closeWebDriver();
  }

  @Test
  public void convertsJavaMethodNameToHumanReadableClause() {
    assertEquals("click", SelenideLogger.readableMethodName("click"));
    assertEquals("set value", SelenideLogger.readableMethodName("setValue"));
    assertEquals("should be", SelenideLogger.readableMethodName("shouldBe"));
    assertEquals("converts java method name to human readable clause", 
        SelenideLogger.readableMethodName("convertsJavaMethodNameToHumanReadableClause"));
  }

  @Test
  public void printsReadableArgumentsValues() {
    assertEquals("", SelenideLogger.readableArguments(null));
    assertEquals("111", SelenideLogger.readableArguments(111));
    assertEquals("[1, 2, 3]", SelenideLogger.readableArguments(1, 2, 3));
    assertEquals("a", SelenideLogger.readableArguments(new String[] {"a"}));
    assertEquals("[a, bb]", SelenideLogger.readableArguments(new String[] {"a", "bb"}));
    assertEquals("null", SelenideLogger.readableArguments(new String[] {null}));
    assertEquals("[null, a, null]", SelenideLogger.readableArguments(new String[] {null, "a", null}));
  }

  @Test
  public void canAddManyListenersPerThread() {
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
    assertThat(value.getElement(), equalTo("div"));
    assertThat(value.getSubject(), equalTo("click()"));
    assertThat(value.getStatus(), equalTo(PASS));
  }
}
