package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;

final class BySelectorCollectionTest implements WithAssertions {
  private final Driver driver = mock(Driver.class);
  private final WebElementSource mockedWebElement = new ElementFinder(driver, null, By.tagName("table"), 3);

  @Test
  void testNoParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, By.id("selenide"));
    String description = bySelectorCollection.description();
    assertThat(description)
      .isEqualTo("By.id: selenide");
  }

  @Test
  void testWithWebElementParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, mockedWebElement, By.name("query"));
    String description = bySelectorCollection.description();
    assertThat(description)
      .isEqualTo("By.tagName: table[3]/By.name: query");
  }
}
