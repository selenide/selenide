package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetDataAttributeCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetDataAttribute getDataAttributeCommand;

  @Before
  public void setup() {
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    getDataAttributeCommand = new GetDataAttribute();
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethodWithDataAttribute() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("data-" + argument)).thenReturn(elementAttribute);
    assertEquals(elementAttribute, getDataAttributeCommand.execute(proxy, locator, new Object[] {argument, "something more"}));
  }

  @Test
  public void testExecuteMethodWithNoDataAttribute() {
    String argument = "class";
    when(mockedElement.getAttribute("data-" + argument)).thenReturn(null);
    assertNull(getDataAttributeCommand.execute(proxy, locator, new Object[] {argument, "something more"}));
  }
}
