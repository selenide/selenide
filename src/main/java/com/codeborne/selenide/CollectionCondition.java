package com.codeborne.selenide;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public abstract class CollectionCondition implements Predicate<List<WebElement>> {
  public abstract void fail(WebElementsCollection collection, List<WebElement> elements, long timeoutMs);

  public static CollectionCondition empty = size(0);

  public static CollectionCondition size(final int expectedSize) {
    return new CollectionCondition() {
      @Override
      public boolean apply(List<WebElement> elements) {
        return elements.size() == expectedSize;
      }

      @Override
      public void fail(WebElementsCollection collection, List<WebElement> elements, long timeoutMs) {
        throw new ListSizeMismatch(expectedSize, collection, elements, timeoutMs);
      }
    };
  }

  public static CollectionCondition texts(final String... expectedTexts) {
    return new CollectionCondition() {
      {
        if (expectedTexts.length == 0) {
          throw new IllegalArgumentException("Array of expected texts is empty");
        }
      }

      @Override
      public boolean apply(List<WebElement> elements) {
        String[] actualTexts = ElementsCollection.getTexts(elements);
        return Arrays.equals(expectedTexts, actualTexts);
      }

      @Override
      public void fail(WebElementsCollection collection, List<WebElement> elements, long timeoutMs) {
        if (elements.isEmpty()) {
          throw new ElementNotFound(collection, expectedTexts, timeoutMs);
        }
        else {
          throw new TextsMismatch(collection, ElementsCollection.getTexts(elements), expectedTexts, timeoutMs);
        }
      }
    };
  }
}
