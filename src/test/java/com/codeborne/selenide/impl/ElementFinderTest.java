package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;

final class ElementFinderTest implements WithAssertions {
  private final Driver driver = mock(Driver.class);

  @Test
  void toStringForFinderByCssSelectors() {
    ElementFinder parent = new ElementFinder(driver, null, By.tagName("table"), 0);
    assertThat(new ElementFinder(driver, null, By.id("app"), 0))
      .hasToString("{By.id: app}");
    assertThat(new ElementFinder(driver, null, By.id("app"), 3))
      .hasToString("{By.id: app[3]}");
    assertThat(new ElementFinder(driver, parent, By.id("app"), 0))
      .hasToString("{By.tagName: table/By.id: app}");
    assertThat(new ElementFinder(driver, parent, By.id("app"), 3))
      .hasToString("{By.tagName: table/By.id: app[3]}");
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
