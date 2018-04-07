package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetInnerHtmlCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetInnerHtml getInnerHtmlCommand;

  @Before
  public void setup() {
    getInnerHtmlCommand = new GetInnerHtml();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethodWhenNotHtmlUnit() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute("innerHTML")).thenReturn(elementAttribute);
    assertEquals(elementAttribute, getInnerHtmlCommand.execute(proxy, locator, new Object[] {argument, "something more"}));
  }
}
