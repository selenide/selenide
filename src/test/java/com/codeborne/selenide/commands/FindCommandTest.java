package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FindCommandTest {

  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement element1;
  private Find findCommand;

  @Before
  public void setup() {
    findCommand = new Find();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testExecuteMethodWithNoArgsPassed() {
    findCommand.execute(proxy, locator);
  }

  @Test
  public void testExecuteMethodWithZeroLengthArgs() {
    when(locator.find(proxy, By.xpath(".."), 0)).thenReturn(element1);
    assertEquals(element1, findCommand.execute(proxy, locator, By.xpath("..")));
  }

  @Test
  public void testExecuteMethodWithMoreThenOneArgsList() {
    when(locator.find(proxy, By.xpath(".."), 1)).thenReturn(element1);
    assertEquals(element1, findCommand.execute(proxy, locator, By.xpath(".."), 1));
  }
}
