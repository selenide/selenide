package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ToWebElementCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private ToWebElement toWebElementCommand;
  private WebElement mockedFoundElement;


  @Before
  public void setup() {
    toWebElementCommand = new ToWebElement();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  public void testExecuteMethod() {
    assertEquals(mockedFoundElement, toWebElementCommand.execute(proxy, locator, new Object[]{}));
  }
}
