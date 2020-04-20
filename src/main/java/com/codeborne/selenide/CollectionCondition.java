package com.codeborne.selenide;

import com.codeborne.selenide.collections.AllMatch;
import com.codeborne.selenide.collections.AnyMatch;
import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.NoneMatch;
import com.codeborne.selenide.collections.SizeGreaterThan;
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual;
import com.codeborne.selenide.collections.SizeLessThan;
import com.codeborne.selenide.collections.SizeLessThanOrEqual;
import com.codeborne.selenide.collections.SizeNotEqual;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.checkerframework.checker.nullness.qual.Nullable;
import com.google.errorprone.annotations.CheckReturnValue;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Predicate;

public abstract class CollectionCondition implements Predicate<List<WebElement>> {
  protected String explanation;

  public abstract void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs);

  public static CollectionCondition empty = size(0);

  /**
   * Checks that collection has the given size
   */
  @CheckReturnValue
  public static CollectionCondition size(int expectedSize) {
    return new ListSize(expectedSize);
  }

  @CheckReturnValue
  public static CollectionCondition sizeGreaterThan(int expectedSize) {
    return new SizeGreaterThan(expectedSize);
  }

  @CheckReturnValue
  public static CollectionCondition sizeGreaterThanOrEqual(int expectedSize) {
    return new SizeGreaterThanOrEqual(expectedSize);
  }

  @CheckReturnValue
  public static CollectionCondition sizeLessThan(int expectedSize) {
    return new SizeLessThan(expectedSize);
  }

  @CheckReturnValue
  public static CollectionCondition sizeLessThanOrEqual(int size) {
    return new SizeLessThanOrEqual(size);
  }

  @CheckReturnValue
  public static CollectionCondition sizeNotEqual(int expectedSize) {
    return new SizeNotEqual(expectedSize);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition texts(String... expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition texts(List<String> expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition textsInAnyOrder(String... expectedTexts) {
    return new TextsInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition textsInAnyOrder(List<String> expectedTexts) {
    return new TextsInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition exactTexts(String... expectedTexts) {
    return new ExactTexts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition exactTexts(List<String> expectedTexts) {
    return new ExactTexts(expectedTexts);
  }

  /**
   * Checks if ANY elements of this collection match the provided predicate
   *
   * @param description The description of the given predicate
   * @param predicate   the {@link java.util.function.Predicate} to match
   */
  @CheckReturnValue
  public static CollectionCondition anyMatch(String description, java.util.function.Predicate<WebElement> predicate) {
    return new AnyMatch(description, predicate);
  }

  /**
   * Checks if ALL elements of this collection match the provided predicate
   *
   * @param description The description of the given predicate
   * @param predicate   the {@link java.util.function.Predicate} to match
   */
  @CheckReturnValue
  public static CollectionCondition allMatch(String description, java.util.function.Predicate<WebElement> predicate) {
    return new AllMatch(description, predicate);
  }

  /**
   * Checks if NONE elements of this collection match the provided predicate
   *
   * @param description The description of the given predicate
   * @param predicate   the {@link java.util.function.Predicate} to match
   */
  @CheckReturnValue
  public static CollectionCondition noneMatch(String description, java.util.function.Predicate<WebElement> predicate) {
    return new NoneMatch(description, predicate);
  }

  /**
   * Wraps CollectionCondition without any changes except toString() method
   * where explanation string (because) are being appended
   */
  private static class ExplainedCollectionCondition extends CollectionCondition {
    private final CollectionCondition delegate;
    private final String message;

    private ExplainedCollectionCondition(CollectionCondition delegate, String message) {
      this.delegate = delegate;
      this.message = message;
    }

    @Override
    public String toString() {
      return delegate.toString() + " (because " + message + ")";
    }

    @Override
    public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
      delegate.fail(collection, elements, lastError, timeoutMs);
    }

    @Override
    public boolean applyNull() {
      return delegate.applyNull();
    }

    @Override
    public boolean test(@Nullable List<WebElement> input) {
      return delegate.test(input);
    }
  }

  /**
   * Should be used for explaining the reason of condition
   */
  public CollectionCondition because(String explanation) {
    this.explanation = explanation;
    return new ExplainedCollectionCondition(this, explanation);
  }

  public abstract boolean applyNull();
}
