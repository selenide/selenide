package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriverException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchesCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private Matches matchesCommand;

  @Before
  public void setup() {
    matchesCommand = new Matches();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
  }

  @Test
  public void testExecuteMethodWhenNoElementFound() {
    when(locator.getWebElement()).thenReturn(null);
    assertFalse(matchesCommand.execute(proxy, locator, new Object[] {Condition.disabled}));
  }

  @Test
  public void testExecuteMethodWhenElementDoesntMeetCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    assertFalse(matchesCommand.execute(proxy, locator, new Object[] {Condition.disabled}));
  }

  @Test
  public void testExecuteMethodWhenElementMeetsCondition() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isEnabled()).thenReturn(true);
    assertTrue(matchesCommand.execute(proxy, locator, new Object[] {Condition.enabled}));
  }

  @Test
  public void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    catchExecuteMethodWithException(new WebDriverException());
  }

  @Test
  public void testExecuteMethodWhenNotFoundExceptionIsThrown() {
    catchExecuteMethodWithException(new NotFoundException());
  }

  @Test
  public void testExecuteMethodWhenIndexOutOfBoundsExceptionIsThrown() {
    catchExecuteMethodWithException(new IndexOutOfBoundsException());
  }

  @Test(expected = InvalidSelectorException.class)
  public void testExecuteMethodWhenExceptionWithInvalidSelectorException() {
    catchExecuteMethodWithException(new NotFoundException("invalid selector"));
  }

  @Test(expected = InvalidSelectorException.class)
  public void testExecuteMethodWhenRunTimeExceptionIsThrown() {
    catchExecuteMethodWithException(new RuntimeException("invalid selector"));
  }

  private <T extends Throwable> void catchExecuteMethodWithException(T exception) {
    doThrow(exception).when(locator).getWebElement();
    assertFalse(matchesCommand.execute(proxy, locator, new Object[] {Condition.enabled}));
  }
}
