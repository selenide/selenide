package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

final class EventsTest {
  private Logger log = mock(Logger.class);
  private Events events = spy(new Events(log));
  private WebElement element = mock(WebElement.class);
  private Driver driver = mock(Driver.class);

  @Test
  void triggersEventsByExecutingJSCode() {
    doNothing().when(events).executeJavaScript(any(), same(element), any());

    events.fireEvent(driver, element, "input", "keyup", "change");

    verify(events).executeJavaScript(driver, element, "input", "keyup", "change");
    verifyNoMoreInteractions(log);
  }

  @Test
  void ignoresStaleElementReferenceException() {
    doThrow(StaleElementReferenceException.class).when(events).executeJavaScript(any(), same(element), any());

    events.fireEvent(driver, element, "change");

    verify(events).executeJavaScript(driver, element, "change");
    verifyNoMoreInteractions(log);
  }

  @Test
  void ignoresButLogs_anyOtherExceptions() {
    doThrow(new UnsupportedOperationException("webdriver does not support JS"))
      .when(events).executeJavaScript(any(), same(element), any());

    events.fireEvent(driver, element, "input", "change");

    verify(events).executeJavaScript(driver, element, "input", "change");
    verify(log).warn("Failed to trigger events {}: {}", asList("input", "change"),
      "java.lang.UnsupportedOperationException: webdriver does not support JS");
  }
}
