package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementFinderTest {
  @Test
  void testToStringForFinderByCssSelectors() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    Assertions.assertEquals("{By.id: app}", new ElementFinder(null, By.id("app"), 0).toString());
    Assertions.assertEquals("{By.id: app[3]}", new ElementFinder(null, By.id("app"), 3).toString());
    Assertions.assertEquals("{By.id: app}", new ElementFinder(parent, By.id("app"), 0).toString());
    Assertions.assertEquals("{By.id: app[3]}", new ElementFinder(parent, By.id("app"), 3).toString());
  }

  @Test
  void testToStringForFinderByXpathExpration() {
    SelenideElement parent = mock(SelenideElement.class);
    when(parent.toString()).thenReturn("table");
    when(parent.getTagName()).thenReturn("table");

    Assertions.assertEquals("{By.xpath: //*[@id='app']}", new ElementFinder(null, By.xpath("//*[@id='app']"), 0).toString());
    Assertions.assertEquals("{By.xpath: //*[@id='app'][3]}", new ElementFinder(null, By.xpath("//*[@id='app']"), 3).toString());
    Assertions.assertEquals("{By.xpath: //*[@id='app']}", new ElementFinder(parent, By.xpath("//*[@id='app']"), 0).toString());
    Assertions.assertEquals("{By.xpath: //*[@id='app'][3]}", new ElementFinder(parent, By.xpath("//*[@id='app']"), 3).toString());
  }
}
