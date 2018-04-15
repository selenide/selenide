package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExistsCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private WebElement element;
  private Exists existsCommand;

  @Before
  public void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element = mock(WebElement.class);
    existsCommand = new Exists();
  }

  @Test
  public void testExistExecuteMethod() {
    when(locator.getWebElement()).thenReturn(null);
    assertFalse(existsCommand.execute(proxy, locator, new Object[]{}));
    when(locator.getWebElement()).thenReturn(element);
    assertTrue(existsCommand.execute(proxy, locator, new Object[]{}));
  }

  @Test
  public void testExistExecuteMethodWithWebDriverException() {
    checkExecuteMethodWithException(new WebDriverException());
  }

  private <T extends Throwable> void checkExecuteMethodWithException(T exception) {
    doThrow(exception).when(locator).getWebElement();
    assertFalse(existsCommand.execute(proxy, locator, new Object[]{}));
  }

  @Test
  public void testExistExecuteMethodElementNotFoundException() {
    checkExecuteMethodWithException(new ElementNotFound("", Condition.appear));
  }

  @Test(expected = InvalidSelectorException.class)
  public void testExistsExecuteMethodInvalidSelectorException() {
    checkExecuteMethodWithException(new InvalidSelectorException("Element is not selectable"));
  }

  @Test
  public void testExistsExecuteMethodWithIndexOutOfBoundException() {
    checkExecuteMethodWithException(new IndexOutOfBoundsException("Out of bound"));
  }
}
