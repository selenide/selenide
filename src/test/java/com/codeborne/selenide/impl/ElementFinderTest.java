package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementFinderTest {
  @Test
  public void testToStringForFinderByCssSelectors() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    assertEquals("{By.id: app}", new ElementFinder(null, By.id("app"), 0).toString());
    assertEquals("{By.id: app[3]}", new ElementFinder(null, By.id("app"), 3).toString());
    assertEquals("{By.id: app}", new ElementFinder(parent, By.id("app"), 0).toString());
    assertEquals("{By.id: app[3]}", new ElementFinder(parent, By.id("app"), 3).toString());
  }

  @Test
  public void testToStringForFinderByXpathExpration() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    assertEquals("{By.xpath: //*[@id='app']}", new ElementFinder(null, By.xpath("//*[@id='app']"), 0).toString());
    assertEquals("{By.xpath: //*[@id='app'][3]}", new ElementFinder(null, By.xpath("//*[@id='app']"), 3).toString());
    assertEquals("{By.xpath: //*[@id='app']}", new ElementFinder(parent, By.xpath("//*[@id='app']"), 0).toString());
    assertEquals("{By.xpath: //*[@id='app'][3]}", new ElementFinder(parent, By.xpath("//*[@id='app']"), 3).toString());
  }
}
