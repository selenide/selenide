package com.codeborne.selenide.commands;

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

class IsDisplayedCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private IsDisplayed isDisplayedCommand;

  @BeforeEach
  void setup() {
    isDisplayedCommand = new IsDisplayed();
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  void testExecuteMethodWhenElementIsNotPresent() {
    Assertions.assertFalse(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}));
  }

  @Test
  void testExecuteMethodWhenElementIsNotDisplayed() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(false);
    Assertions.assertFalse(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}));
  }

  @Test
  void testExecuteMethodWhenElementIsDisplayed() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isDisplayed()).thenReturn(true);
    Assertions.assertTrue(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}));
  }

  @Test
  void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    catchExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void catchExecuteMethodWithException(T exception) {
    when(locator.getWebElement()).thenReturn(mockedElement);
    doThrow(exception).when(mockedElement).isDisplayed();
    Assertions.assertFalse(isDisplayedCommand.execute(proxy, locator, new Object[]{"something more"}));
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
}
