package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebElementWaitingProxyTest {
  @Test
  public void testToString() {
    WebElement parent = mock(WebElement.class);
    when(parent.toString()).thenReturn("table");

    assertEquals("WebElementWaitingProxy{By.id: app}", new WebElementWaitingProxy(null, By.id("app"), 0).toString());
    assertEquals("WebElementWaitingProxy{By.id: app, index: 3}", new WebElementWaitingProxy(null, By.id("app"), 3).toString());
    assertEquals("WebElementWaitingProxy{By.id: app, in: table}", new WebElementWaitingProxy(parent, By.id("app"), 0).toString());
    assertEquals("WebElementWaitingProxy{By.id: app, in: table, index: 3}", new WebElementWaitingProxy(parent, By.id("app"), 3).toString());
  }
}
