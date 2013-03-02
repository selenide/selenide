package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WaitingSelenideElementTest {
  @Test
  public void testToString() {
    WebElement parent = mock(WebElement.class);
    when(parent.toString()).thenReturn("table");

    assertEquals("WaitingSelenideElement{By.id: app}", new WaitingSelenideElement(null, By.id("app"), 0).toString());
    assertEquals("WaitingSelenideElement{By.id: app, index: 3}", new WaitingSelenideElement(null, By.id("app"), 3).toString());
    assertEquals("WaitingSelenideElement{By.id: app, in: table}", new WaitingSelenideElement(parent, By.id("app"), 0).toString());
    assertEquals("WaitingSelenideElement{By.id: app, in: table, index: 3}", new WaitingSelenideElement(parent, By.id("app"), 3).toString());
  }
}
