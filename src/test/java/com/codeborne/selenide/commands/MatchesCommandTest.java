package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MatchesCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private Matches matchesCommand;

  @BeforeEach
  void setup() {
    matchesCommand = new Matches();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
  }

  @Test
  void testExecuteMethodWhenNoElementFound() {
    when(locator.getWebElement()).thenReturn(null);
    Assertions.assertFalse(matchesCommand.execute(proxy, locator, new Object[]{Condition.disabled}));
  }

  @Test
  void testExecuteMethodWhenElementDoesntMeetCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    Assertions.assertFalse(matchesCommand.execute(proxy, locator, new Object[]{Condition.disabled}));
  }

  @Test
  void testExecuteMethodWhenElementMeetsCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    Assertions.assertTrue(matchesCommand.execute(proxy, locator, new Object[]{Condition.enabled}));
  }

  @Test
  void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    catchExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void catchExecuteMethodWithException(T exception) {
    doThrow(exception).when(locator).getWebElement();
    Assertions.assertFalse(matchesCommand.execute(proxy, locator, new Object[]{Condition.enabled}));
  }

  @Test
  void testExecuteMethodWhenNotFoundExceptionIsThrown() {
    catchExecuteMethodWithException(new NotFoundException());
  }

  @Test
  void testExecuteMethodWhenIndexOutOfBoundsExceptionIsThrown() {
    catchExecuteMethodWithException(new IndexOutOfBoundsException());
  }

  @Test
  void testExecuteMethodWhenExceptionWithInvalidSelectorException() {
    Assertions.assertThrows(InvalidSelectorException.class,
      () -> catchExecuteMethodWithException(new NotFoundException("invalid selector")));
  }

  @Test
  void testExecuteMethodWhenRunTimeExceptionIsThrown() {
    Assertions.assertThrows(InvalidSelectorException.class,
      () -> catchExecuteMethodWithException(new RuntimeException("invalid selector")));
  }
}
