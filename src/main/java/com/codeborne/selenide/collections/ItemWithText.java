package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class ItemWithText extends CollectionCondition {

  private final String expectedText;

  public ItemWithText(String expectedText) {
    this.expectedText = expectedText;
  }

  @Override
  public boolean test(List<WebElement> elements) {
    return ElementsCollection
      .texts(elements)
      .contains(expectedText);
  }

  @Override
  public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
    throw new ElementWithTextNotFound(
      collection, ElementsCollection.texts(elements), Collections.singletonList(expectedText), explanation, timeoutMs);
  }

  @Override
  public boolean applyNull() {
    return false;
  }

  @Override
  public String toString() {
    return "Text " + expectedText;
  }
}
