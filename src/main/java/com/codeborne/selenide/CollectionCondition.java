package com.codeborne.selenide;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public abstract class CollectionCondition implements Predicate<List<WebElement>> {
  public abstract void fail(List<WebElement> elements, long timeoutMs);

  public static CollectionCondition empty = size(0);

  public static CollectionCondition size(final int expectedSize) {
    return new CollectionCondition() {
      @Override
      public boolean apply(List<WebElement> elements) {
        return elements.size() == expectedSize;
      }

      @Override
      public void fail(List<WebElement> elements, long timeoutMs) {
        throw new ListSizeNotMatched(expectedSize, elements, timeoutMs);
      }
    };
  }

  public static CollectionCondition texts(final String... expectedTexts) {
    return new CollectionCondition() {
      @Override
      public boolean apply(List<WebElement> elements) {
        int i = 0;
        for (WebElement element : elements) {
          if (!expectedTexts[i++].equals(element.getText())) {
            return false;
          }
        }
        return true;
      }

      @Override
      public void fail(List<WebElement> elements, long timeoutMs) {
        WebDriverRunner.fail("Elements' texts are " + Arrays.toString(ElementsCollection.getTexts(elements)) + ", but expected texts are " + Arrays.toString(expectedTexts));
      }
    };
  }
}
