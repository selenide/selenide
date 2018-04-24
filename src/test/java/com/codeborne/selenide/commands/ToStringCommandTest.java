package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ToStringCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private ToString toStringCommand;
  private WebElement mockedFoundElement;

  @Before
  public void setup() {
    toStringCommand = new ToString();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  public void testExecuteMethod() {
    when(mockedFoundElement.isSelected()).thenReturn(true);
    when(mockedFoundElement.isDisplayed()).thenReturn(true);
    String elementText = "text";
    when(mockedFoundElement.getText()).thenReturn(elementText);
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertEquals("<null selected:true>text</null>", elementString);
  }

  @Test
  public void testExecuteMethodWhenWebDriverExceptionIsThrown() {
    doThrow(new WebDriverException()).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertTrue(elementString.contains("WebDriverException"));
  }

  @Test
  public void testExecuteMethodWhenElementNotFoundIsThrown() {
    doThrow(new ElementNotFound(By.name(""), Condition.visible)).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertEquals("Element not found {By.name: }", elementString);
  }

  @Test
  public void testExecuteMethodWhenIndexOutOfBoundExceptionIsThrown() {
    doThrow(new IndexOutOfBoundsException()).when(locator).getWebElement();
    String elementString = toStringCommand.execute(proxy, locator, new Object[]{});
    assertEquals("java.lang.IndexOutOfBoundsException", elementString);
  }
}
