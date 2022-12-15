package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class BySelectorCollectionTest {
  private final Driver driver = mock();
  private final WebElementSource webElement = new ElementFinder(driver, null, By.tagName("table"), 3);

  @Test
  void constructorWithoutParent() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, By.name("query"));
    assertThat(bySelectorCollection.description()).isEqualTo("By.name: query");
  }

  @Test
  void constructorWithParent() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, webElement, By.name("query"));
    assertThat(bySelectorCollection.description()).isEqualTo("By.tagName: table[3]/By.name: query");
  }
}
