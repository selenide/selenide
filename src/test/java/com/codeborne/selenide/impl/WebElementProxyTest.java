package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebElementProxyTest {
  @Test
  public void testToString() {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn("h2");
    when(element.toString()).thenReturn("webElement");
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    assertEquals("<h2></h2>", new WebElementProxy(element).toString());
  }
}
