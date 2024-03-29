package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class ElementFinderTest {
  private final Driver driver = mock();

  @Test
  void toStringForFinderByCssSelectors() {
    ElementFinder parent = new ElementFinder(driver, null, By.tagName("table"), 0);
    assertThat(new ElementFinder(driver, null, By.id("app"), 0))
      .hasToString("{#app}");
    assertThat(new ElementFinder(driver, null, By.id("app"), 3))
      .hasToString("{#app[3]}");
    assertThat(new ElementFinder(driver, parent, By.id("app"), 0))
      .hasToString("{table/#app}");
    assertThat(new ElementFinder(driver, parent, By.id("app"), 3))
      .hasToString("{table/#app[3]}");
  }

  @Test
  void toStringForFinderByXpathExpiration() {
    ElementFinder parent = new ElementFinder(driver, null, By.xpath("//table"), 0);

    assertThat(new ElementFinder(driver, null, By.xpath("//*[@id='app']"), 0))
      .hasToString("{By.xpath: //*[@id='app']}");
    assertThat(new ElementFinder(driver, null, By.xpath("//*[@id='app']"), 3))
      .hasToString("{By.xpath: //*[@id='app'][3]}");
    assertThat(new ElementFinder(driver, parent, By.xpath("//*[@id='app']"), 0))
      .hasToString("{By.xpath: //table/By.xpath: //*[@id='app']}");
    assertThat(new ElementFinder(driver, parent, By.xpath("//*[@id='app']"), 3))
      .hasToString("{By.xpath: //table/By.xpath: //*[@id='app'][3]}");
  }
}
