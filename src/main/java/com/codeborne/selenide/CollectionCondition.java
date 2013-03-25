package com.codeborne.selenide;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.Collection;

import static com.codeborne.selenide.ElementsCollection.getTexts;

public abstract class CollectionCondition implements Predicate<Collection<SelenideElement>> {
  abstract void fail(Collection<SelenideElement> elements);

  public static CollectionCondition size(final int expectedSize) {
    return new CollectionCondition() {
      @Override
      public boolean apply(Collection<SelenideElement> elements) {
        return elements.size() == expectedSize;
      }

      @Override
      public void fail(Collection<SelenideElement> elements) {
        WebDriverRunner.fail("List size is " + elements.size() + ", but expected size is " + expectedSize);
      }
    };
  }

  public static CollectionCondition texts(final String... expectedTexts) {
    return new CollectionCondition() {
      @Override
      public boolean apply(Collection<SelenideElement> elements) {
        int i = 0;
        for (WebElement element : elements) {
          if (!expectedTexts[i++].equals(element.getText())) {
            return false;
          }
        }
        return true;
      }

      @Override
      public void fail(Collection<SelenideElement> elements) {
        WebDriverRunner.fail("Elements' texts are " + Arrays.toString(getTexts(elements)) + ", but expected texts are " + Arrays.toString(expectedTexts));
      }
    };
  }
}
