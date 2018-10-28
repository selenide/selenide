package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.SizeGreaterThan;
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual;
import com.codeborne.selenide.collections.SizeLessThan;
import com.codeborne.selenide.collections.SizeLessThanOrEqual;
import com.codeborne.selenide.collections.SizeNotEqual;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class CollectionCondition implements Predicate<List<WebElement>> {
  protected String explanation;

  public abstract void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs);

  public static CollectionCondition empty = size(0);

  /**
   * Checks that collection has the given size
   */
  public static CollectionCondition size(int expectedSize) {
    return new ListSize(expectedSize);
  }

  public static CollectionCondition sizeGreaterThan(int expectedSize) {
    return new SizeGreaterThan(expectedSize);
  }

  public static CollectionCondition sizeGreaterThanOrEqual(int expectedSize) {
    return new SizeGreaterThanOrEqual(expectedSize);
  }

  public static CollectionCondition sizeLessThan(int expectedSize) {
    return new SizeLessThan(expectedSize);
  }

  public static CollectionCondition sizeLessThanOrEqual(int size) {
    return new SizeLessThanOrEqual(size);
  }

  public static CollectionCondition sizeNotEqual(int expectedSize) {
    return new SizeNotEqual(expectedSize);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition texts(String... expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition texts(List<String> expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition textsInAnyOrder(String... expectedTexts) {
    return new TextsInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition textsInAnyOrder(List<String> expectedTexts) {
    return new TextsInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition exactTexts(String... expectedTexts) {
    return new ExactTexts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  public static CollectionCondition exactTexts(List<String> expectedTexts) {
    return new ExactTexts(expectedTexts);
  }

  /**
   * Should be used for explaining the reason of condition
   */
  public CollectionCondition because(String explanation) {
    this.explanation = explanation;
    return this;
  }

  public abstract boolean applyNull();
}
