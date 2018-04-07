package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetInnerTextCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetInnerText getInnerTextCommand;

  @Before
  public void setup() {
    getInnerTextCommand = new GetInnerText();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethodWhenNotIE() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("textContent")).thenReturn(elementAttribute);
    assertEquals(elementAttribute, getInnerTextCommand.execute(proxy, locator, new Object[] {argument, "something more"}));
  }
}
