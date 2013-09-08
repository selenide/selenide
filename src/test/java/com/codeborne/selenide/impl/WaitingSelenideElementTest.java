package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WaitingSelenideElementTest {
  @Test
  public void testToString() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    assertEquals("{By.id: app}", new WaitingSelenideElement(null, By.id("app"), 0).toString());
    assertEquals("{By.id: app, index: 3}", new WaitingSelenideElement(null, By.id("app"), 3).toString());
    assertEquals("{By.id: app, in: <table></table>}", new WaitingSelenideElement(parent, By.id("app"), 0).toString());
    assertEquals("{By.id: app, in: <table></table>, index: 3}", new WaitingSelenideElement(parent, By.id("app"), 3).toString());
  }
}
