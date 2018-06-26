package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExistsCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private WebElement element;
  private Exists existsCommand;

  @BeforeEach
  void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element = mock(WebElement.class);
    existsCommand = new Exists();
  }

  @Test
  void testExistExecuteMethod() {
    when(locator.getWebElement()).thenReturn(null);
    Assertions.assertFalse(existsCommand.execute(proxy, locator, new Object[]{}));
    when(locator.getWebElement()).thenReturn(element);
    Assertions.assertTrue(existsCommand.execute(proxy, locator, new Object[]{}));
  }

  @Test
  void testExistExecuteMethodWithWebDriverException() {
    checkExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void checkExecuteMethodWithException(T exception) {
    doThrow(exception).when(locator).getWebElement();
    Assertions.assertFalse(existsCommand.execute(proxy, locator, new Object[]{}));
  }

  @Test
  void testExistExecuteMethodElementNotFoundException() {
    checkExecuteMethodWithException(new ElementNotFound("", Condition.appear));
  }

  @Test
  void testExistsExecuteMethodInvalidSelectorException() {
    Assertions.assertThrows(InvalidSelectorException.class,
      () -> checkExecuteMethodWithException(new InvalidSelectorException("Element is not selectable")));
  }

  @Test
  void testExistsExecuteMethodWithIndexOutOfBoundException() {
    checkExecuteMethodWithException(new IndexOutOfBoundsException("Out of bound"));
  }
}
