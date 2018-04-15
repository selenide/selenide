package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.openqa.selenium.By;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetClosestCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private GetClosest getClosestCommand;

  @Before
  public void setup() {
    getClosestCommand = new GetClosest();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
  }

  @Test
  public void testExecuteMethodWithTagsStartsWithDot() {
    String argument = ".class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute(argument)).thenReturn(elementAttribute);
    when(locator.find(proxy,
      By.xpath(
        String.format("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][1]",
          argument.substring(1))),
      0)).
      thenReturn(mockedElement);
    assertEquals(mockedElement, getClosestCommand.execute(proxy, locator, new Object[]{argument, "something more"}));
  }

  @Test
  public void testExecuteMethodWithTagsThatDontStartsWithDot() {
    String argument = "class";
    String elementAttribute = "hello";
    when(mockedElement.getAttribute(argument)).thenReturn(elementAttribute);
    when(locator.find(proxy, By.xpath(String.format("ancestor::%s[1]", argument)), 0)).thenReturn(mockedElement);
    assertEquals(mockedElement, getClosestCommand.execute(proxy, locator, new Object[]{argument, "something more"}));
  }
}
