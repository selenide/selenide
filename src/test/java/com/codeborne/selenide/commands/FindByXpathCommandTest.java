package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FindByXpathCommandTest {

  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement element1;
  private FindByXpath findByXpathCommand;

  @Before
  public void setup() {
    findByXpathCommand = new FindByXpath();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testExecuteMethodWithNoArgsPassed() {
    findByXpathCommand.execute(proxy, locator);
  }

  @Test
  public void testExecuteMethodWithZeroLengthArgs() {
    when(locator.find(proxy, By.xpath("."), 0)).thenReturn(element1);
    assertEquals(element1, findByXpathCommand.execute(proxy, locator, "."));
  }

  @Test
  public void testExecuteMethodWithMoreThenOneArgsList() {
    when(locator.find(proxy, By.xpath("."), 1)).thenReturn(element1);
    assertEquals(element1, findByXpathCommand.execute(proxy, locator, ".", 1));
  }
}
