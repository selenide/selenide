package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class CollectionCondition implements Predicate<List<WebElement>> {
  public abstract void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs);

  public static final CollectionCondition empty = size(0);

  public static CollectionCondition size(final int expectedSize) {
    return new ListSize(expectedSize);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition texts(final String... expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition exactTexts(final String... expectedTexts) {
    return new ExactTexts(expectedTexts);
  }
}
