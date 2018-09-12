package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.logging.Logger;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class EventsTest {
  private Events events = spy(new Events());
  private WebElement element = mock(WebElement.class);
  private Driver driver = mock(Driver.class);

  @BeforeEach
  void setUp() {
    events.log = mock(Logger.class);
  }

  @Test
  void triggersEventsByExecutingJSCode() {
    doNothing().when(events).executeJavaScript(any(), same(element), any());

    events.fireEvent(driver, element, "input", "keyup", "change");

    verify(events).executeJavaScript(driver, element, "input", "keyup", "change");
    verifyNoMoreInteractions(events.log);
  }

  @Test
  void ignoresStaleElementReferenceException() {
    doThrow(StaleElementReferenceException.class).when(events).executeJavaScript(any(), same(element), any());

    events.fireEvent(driver, element, "change");

    verify(events).executeJavaScript(driver, element, "change");
    verifyNoMoreInteractions(events.log);
  }

  @Test
  void ignoresButLogs_anyOtherExceptions() {
    doThrow(new UnsupportedOperationException("webdriver does not support JS"))
      .when(events).executeJavaScript(any(), same(element), any());

    events.fireEvent(driver, element, "input", "change");

    verify(events).executeJavaScript(driver, element, "input", "change");
    verify(events.log).warning("Failed to trigger events [input, change]: " +
      "java.lang.UnsupportedOperationException: webdriver does not support JS");
  }
}
