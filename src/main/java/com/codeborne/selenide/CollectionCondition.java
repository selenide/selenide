package com.codeborne.selenide;

import com.codeborne.selenide.collections.*;
import com.codeborne.selenide.ex.ExplainedUIAssertionError;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CollectionCondition implements Predicate<List<WebElement>> {
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

  public CollectionCondition because(String message) {
    return new ExplainedCollectionCondition(this, message);
  }

  public static class ExplainedCollectionCondition extends CollectionCondition {
    private final CollectionCondition delegate;
    private final String message;

    private ExplainedCollectionCondition(CollectionCondition delegate, String message) {
      this.delegate = delegate;
      this.message = message;
    }

    @Override
    public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
      try {
        delegate.fail(collection, elements, lastError, timeoutMs);
      } catch (UIAssertionError expected) {
        throw new ExplainedUIAssertionError(message, expected);
      }
    }

    @Override
    public boolean apply(@Nullable List<WebElement> input) {
      return delegate.apply(input);
    }

    @Override
    public String toString() {
      return delegate.toString() + " (because " + message + ")";
    }
  }

}
