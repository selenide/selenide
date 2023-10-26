package com.codeborne.selenide;

import com.codeborne.selenide.collections.AllMatch;
import com.codeborne.selenide.collections.AnyMatch;
import com.codeborne.selenide.collections.Attributes;
import com.codeborne.selenide.collections.ContainExactTextsCaseSensitive;
import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ExactTextsCaseSensitive;
import com.codeborne.selenide.collections.ExactTextsCaseSensitiveInAnyOrder;
import com.codeborne.selenide.collections.ItemWithText;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.NoneMatch;
import com.codeborne.selenide.collections.SizeGreaterThan;
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual;
import com.codeborne.selenide.collections.SizeLessThan;
import com.codeborne.selenide.collections.SizeLessThanOrEqual;
import com.codeborne.selenide.collections.SizeNotEqual;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.Arrays.asList;

@ParametersAreNonnullByDefault
public final class CollectionCondition {
  public static WebElementsCondition empty = size(0);

  /**
   * Checks that collection has the given size
   */
  @CheckReturnValue
  public static WebElementsCondition size(int expectedSize) {
    return new ListSize(expectedSize);
  }

  @CheckReturnValue
  public static WebElementsCondition sizeGreaterThan(int expectedSize) {
    return new SizeGreaterThan(expectedSize);
  }

  @CheckReturnValue
  public static WebElementsCondition sizeGreaterThanOrEqual(int expectedSize) {
    return new SizeGreaterThanOrEqual(expectedSize);
  }

  @CheckReturnValue
  public static WebElementsCondition sizeLessThan(int expectedSize) {
    return new SizeLessThan(expectedSize);
  }

  @CheckReturnValue
  public static WebElementsCondition sizeLessThanOrEqual(int size) {
    return new SizeLessThanOrEqual(size);
  }

  @CheckReturnValue
  public static WebElementsCondition sizeNotEqual(int expectedSize) {
    return new SizeNotEqual(expectedSize);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition texts(String... expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition texts(List<String> expectedTexts) {
    return new Texts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition textsInAnyOrder(String... expectedTexts) {
    return new TextsInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition textsInAnyOrder(List<String> expectedTexts) {
    return new TextsInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition exactTexts(String... expectedTexts) {
    return new ExactTexts(expectedTexts);
  }

  /**
   * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition exactTexts(List<String> expectedTexts) {
    return new ExactTexts(expectedTexts);
  }

  /**
   * @see #attributes(String, List)
   */
  @CheckReturnValue
  public static WebElementsCondition attributes(String attribute, String... expectedValues) {
    return attributes(attribute, asList(expectedValues));
  }

  /**
   * Checks that given collection has given attribute values (each collection element EQUALS TO corresponding attribute value)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition attributes(String attribute, List<String> expectedValues) {
    return new Attributes(attribute, expectedValues);
  }

  /**
   * Checks that given collection has given case-sensitive texts (each collection element EQUALS TO CASE SENSITIVE corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition exactTextsCaseSensitive(String... expectedTexts) {
    return new ExactTextsCaseSensitive(expectedTexts);
  }

  /**
   * Checks that given collection has given case-sensitive texts (each collection element EQUALS TO CASE SENSITIVE corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static WebElementsCondition exactTextsCaseSensitive(List<String> expectedTexts) {
    return new ExactTextsCaseSensitive(expectedTexts);
  }

  /**
   * Checks if ANY elements of this collection match the provided predicate
   *
   * @param description The description of the given predicate
   * @param predicate   the {@link java.util.function.Predicate} to match
   */
  @CheckReturnValue
  public static WebElementsCondition anyMatch(String description, java.util.function.Predicate<WebElement> predicate) {
    return new AnyMatch(description, predicate);
  }

  /**
   * Checks if ALL elements of this collection match the provided predicate
   *
   * @param description The description of the given predicate
   * @param predicate   the {@link java.util.function.Predicate} to match
   */
  @CheckReturnValue
  public static WebElementsCondition allMatch(String description, java.util.function.Predicate<WebElement> predicate) {
    return new AllMatch(description, predicate);
  }

  /**
   * Checks if NONE elements of this collection match the provided predicate
   *
   * @param description The description of the given predicate
   * @param predicate   the {@link java.util.function.Predicate} to match
   */
  @CheckReturnValue
  public static WebElementsCondition noneMatch(String description, java.util.function.Predicate<WebElement> predicate) {
    return new NoneMatch(description, predicate);
  }

  /**
   * Checks if given collection has an element with given text.
   * The condition is satisfied if one or more elements in this collection have exactly the given text.
   *
   * @param expectedText The expected text in the collection
   */
  @CheckReturnValue
  public static WebElementsCondition itemWithText(String expectedText) {
    return new ItemWithText(expectedText);
  }

  /**
   * Check that the given collection contains all elements with given texts.
   * <p> NB! This condition is case-sensitive and checks for exact matches! </p>
   * Examples:
   * <pre>
   * {@code
   * // collection 1: [Tom, Dick, Harry]
   * $$("li.odd").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
   *
   * // collection 2: [Tom, John, Dick, Harry]
   * $$("li.even").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
   *
   * // collection 3: [John, Dick, Tom, Paul]
   * $$("li.first").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
   *
   * // collection 4: [Tom, Dick, hArRy]
   * $$("li.last").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
   * }
   * </pre>
   *
   * @param expectedTexts the expected texts that the collection should contain
   */
  @CheckReturnValue
  public static WebElementsCondition containExactTextsCaseSensitive(String... expectedTexts) {
    return new ContainExactTextsCaseSensitive(expectedTexts);
  }

  /**
   * Check that the given collection contains all elements with given texts.
   * <p> NB! This condition is case-sensitive and checks for exact matches! </p>
   *
   * Examples:
   *
   * <pre>
   * {@code
   * // collection 1: [Tom, Dick, Harry]
   * $$("li.odd").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
   *
   * // collection 2: [Tom, John, Dick, Harry]
   * $$("li.even").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
   *
   * // collection 3: [John, Dick, Tom, Paul]
   * $$("li.first").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
   *
   * // collection 4: [Tom, Dick, hArRy]
   * $$("li.last").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
   *
   * }
   * </pre>
   *
   * @param expectedTexts the expected texts that the collection should contain
   */
  @CheckReturnValue
  public static WebElementsCondition containExactTextsCaseSensitive(List<String> expectedTexts) {
    return new ContainExactTextsCaseSensitive(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Case sensitive</p>
   *
   * @param expectedTexts Expected texts in any order in the collection
   */
  @CheckReturnValue
  public static WebElementsCondition exactTextsCaseSensitiveInAnyOrder(List<String> expectedTexts) {
    return new ExactTextsCaseSensitiveInAnyOrder(expectedTexts);
  }

  /**
   * Checks that given collection has given texts in any order (each collection element EQUALS TO corresponding text)
   *
   * <p>NB! Case sensitive</p>
   *
   * @param expectedTexts Expected texts in any order in the collection
   */
  @CheckReturnValue
  public static WebElementsCondition exactTextsCaseSensitiveInAnyOrder(String... expectedTexts) {
    return new ExactTextsCaseSensitiveInAnyOrder(expectedTexts);
  }
}
