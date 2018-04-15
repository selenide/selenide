package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.openqa.selenium.WebElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAttributeCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private WebElement mockedElement;

  @Before
  public void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethod() {
    GetAttribute getAttributeCommand = new GetAttribute();
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute(argument)).thenReturn(elementAttribute);
    assertEquals(elementAttribute,
      getAttributeCommand.execute(proxy, locator, new Object[]{argument, "something more"}));
  }
}
