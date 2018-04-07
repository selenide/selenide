package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IsImageCommandTest {
  private SelenideElement proxy = mock(SelenideElement.class);
  private WebElementSource locator = mock(WebElementSource.class);
  private SelenideElement mockedElement = mock(SelenideElement.class);
  private IsImage isImageCommand;

  @Before
  public void setup() {
    isImageCommand = new IsImage();
    when(locator.getWebElement()).thenReturn(mockedElement);
  }

  @Test
  public void testExecuteMethodWhenElementIsNotImage() {
    when(mockedElement.getTagName()).thenReturn("href");
    try {
      isImageCommand.execute(proxy, locator, new Object[] {"something more"});
    } catch (IllegalArgumentException exception) {
      assertEquals("Method isImage() is only applicable for img elements", exception.getMessage());
    }
  }
}
