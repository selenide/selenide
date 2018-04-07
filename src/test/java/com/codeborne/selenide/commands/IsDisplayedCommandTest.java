package com.codeborne.selenide.commands;

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

public class IsDisplayedCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private IsDisplayed isDisplayedCommand;

  @Before
  public void setup() {
    isDisplayedCommand = new IsDisplayed();
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethodWhenElementIsNotPresent() {
    assertFalse(isDisplayedCommand.execute(proxy, locator, new Object[] {"something more"}));
  }

  @Test
  public void testExecuteMethodWhenElementIsNotDisplayed() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(false);
    assertFalse(isDisplayedCommand.execute(proxy, locator, new Object[] {"something more"}));
  }

  @Test
  public void testExecuteMethodWhenElementIsDisplayed() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(true);
    assertTrue(isDisplayedCommand.execute(proxy, locator, new Object[] {"something more"}));
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

  private <T extends Throwable> void catchExecuteMethodWithException(T exception) {
    when(locator.getWebElement()).thenReturn(mockedElement);
    doThrow(exception).when(mockedElement).isDisplayed();
    assertFalse(isDisplayedCommand.execute(proxy, locator, new Object[] {"something more"}));
  }


}
