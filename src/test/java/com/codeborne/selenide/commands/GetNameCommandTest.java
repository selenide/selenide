package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetNameCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetName getNameCommand;

  @Before
  public void setup() {
    getNameCommand = new GetName();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethod() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("name")).thenReturn(elementAttribute);
    assertEquals(elementAttribute, getNameCommand.execute(proxy, locator, new Object[] {argument, "something more"}));
  }
}
