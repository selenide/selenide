package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetWrappedElementCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private GetWrappedElement getWrappedElementCommand;

  @Before
  public void setup() {
    getWrappedElementCommand = new GetWrappedElement();
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethod() {
    assertEquals(mockedElement, getWrappedElementCommand.execute(proxy, locator, new Object[] {"something more"}));
  }
}
