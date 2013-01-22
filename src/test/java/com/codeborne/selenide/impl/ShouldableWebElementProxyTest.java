package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShouldableWebElementProxyTest {
  @Test
  public void testToString() {
    WebElement element = mock(WebElement.class);
    when(element.toString()).thenReturn("webElement");
    assertEquals("ShouldableWebElementProxy{webElement}", new ShouldableWebElementProxy(element).toString());
  }
}
