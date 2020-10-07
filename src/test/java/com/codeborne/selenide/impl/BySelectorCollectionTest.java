package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class BySelectorCollectionTest implements WithAssertions {
  private final Driver driver = mock(Driver.class);
  private final SelenideElement mockedWebElement = mock(SelenideElement.class);

  @Test
  void testNoParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, By.id("selenide"));
    String description = bySelectorCollection.description();
    assertThat(description)
      .isEqualTo("By.id: selenide");
  }

  @Test
  void testWithWebElementParentConstructor() {
    when(mockedWebElement.getSearchCriteria()).thenReturn("By.tagName: a");

    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, mockedWebElement, By.name("selenide"));
    String description = bySelectorCollection.description();
    assertThat(description)
      .isEqualTo("By.tagName: a/By.name: selenide");
  }

  @Test
  void testWithNotWebElementParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(driver, new NotWebElement(), By.name("selenide"));
    String description = bySelectorCollection.description();
    assertThat(description)
      .isEqualTo("By.name: selenide");
  }

  private class NotWebElement implements SearchContext {
    @Override
    public List<WebElement> findElements(By by) {
      return singletonList(mockedWebElement);
    }

    @Override
    public WebElement findElement(By by) {
      return mockedWebElement;
    }
  }
}
