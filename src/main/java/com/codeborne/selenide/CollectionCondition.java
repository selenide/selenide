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
import com.codeborne.selenide.impl.CollectionSource;
import com.github.bsideup.jabel.Desugar;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static java.util.Arrays.asList;

@ParametersAreNonnullByDefault
public abstract class CollectionCondition {
  protected String explanation;

  /**
   * @deprecated Please implement method {@link #check(CollectionSource)} instead.
   */
  @Deprecated
  @CheckReturnValue
  public boolean test(List<WebElement> elements) {
    throw new UnsupportedOperationException("Implement method 'check' (powerful) or 'test' (simple)");
  }

  @Nonnull
  @CheckReturnValue
  public CheckResult check(Driver driver, List<WebElement> elements) {
    boolean result = test(elements);
    return result ? new CheckResult(ACCEPT, null) : new CheckResult(REJECT, new ActualElementsHolder(elements));
  }

  /**
   * The most powerful way to implement condition.
   * Can check the collection using JavaScript or any other effective means.
   * Also, can return "actual values" in the returned {@link CheckResult} object.
   */
  @Nonnull
  @CheckReturnValue
  public CheckResult check(CollectionSource collection) {
    List<WebElement> elements = collection.getElements();
    return check(collection.driver(), elements);
  }

  @Deprecated
  public void fail(CollectionSource collection,
                   @Nullable List<WebElement> elements,
                   @Nullable Exception cause,
                   long timeoutMs) {
    throw new UnsupportedOperationException(
      "Implement method 'fail(..CheckResult..)' (powerful) or 'fail(..List<WebElement>..)' (simple)");
  }

  /**
   * Every subclass should implement this method.
   * We left here the default implementation only because of backward compatibility.
   */
  public void fail(CollectionSource collection,
                   CheckResult lastCheckResult,
                   @Nullable Exception cause,
                   long timeoutMs) {
    List<WebElement> actualElements = lastCheckResult.actualValue() instanceof ActualElementsHolder holder ? holder.elements() : null;
    fail(collection, actualElements, cause, timeoutMs);
  }

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
   * @see #attributes(String, List)
   */
  @CheckReturnValue
  public static CollectionCondition attributes(String attribute, String... expectedValues) {
    return attributes(attribute, asList(expectedValues));
  }

  /**
   * Checks that given collection has given attribute values (each collection element EQUALS TO corresponding attribute value)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition attributes(String attribute, List<String> expectedValues) {
    return new Attributes(attribute, expectedValues);
  }

  /**
   * Checks that given collection has given case-sensitive texts (each collection element EQUALS TO CASE SENSITIVE corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition exactTextsCaseSensitive(String... expectedTexts) {
    return new ExactTextsCaseSensitive(expectedTexts);
  }

  /**
   * Checks that given collection has given case-sensitive texts (each collection element EQUALS TO CASE SENSITIVE corresponding text)
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  public static CollectionCondition exactTextsCaseSensitive(List<String> expectedTexts) {
    return new ExactTextsCaseSensitive(expectedTexts);
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
   * Checks if given collection has an element with given text.
   * The condition is satisfied if one or more elements in this collection have exactly the given text.
   *
   * @param expectedText The expected text in the collection
   */
  @CheckReturnValue
  public static CollectionCondition itemWithText(String expectedText) {
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
  public static CollectionCondition containExactTextsCaseSensitive(String... expectedTexts) {
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
  public static CollectionCondition containExactTextsCaseSensitive(List<String> expectedTexts) {
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
  public static CollectionCondition exactTextsCaseSensitiveInAnyOrder(List<String> expectedTexts) {
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
  public static CollectionCondition exactTextsCaseSensitiveInAnyOrder(String... expectedTexts) {
    return new ExactTextsCaseSensitiveInAnyOrder(expectedTexts);
  }

  /**
   * Wraps CollectionCondition without any changes except toString() method
   * where explanation string (because) are being appended
   */
  @ParametersAreNonnullByDefault
  private static class ExplainedCollectionCondition extends CollectionCondition {
    private final CollectionCondition delegate;
    private final String message;

    private ExplainedCollectionCondition(CollectionCondition delegate, String message) {
      this.delegate = delegate;
      this.message = message;
    }

    @Override
    public String toString() {
      return delegate + " (because " + message + ")";
    }

    @Override
    public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
      delegate.fail(collection, lastCheckResult, cause, timeoutMs);
    }

    @Override
    @Deprecated
    public void fail(CollectionSource collection,
                     @Nullable List<WebElement> elements,
                     @Nullable Exception cause,
                     long timeoutMs) {
      delegate.fail(collection, elements, cause, timeoutMs);
    }

    @Override
    public boolean missingElementSatisfiesCondition() {
      return delegate.missingElementSatisfiesCondition();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean test(List<WebElement> input) {
      return delegate.test(input);
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CheckResult check(CollectionSource collection) {
      return delegate.check(collection);
    }
  }

  /**
   * Should be used for explaining the reason of condition
   */
  public CollectionCondition because(String explanation) {
    this.explanation = explanation;
    return new ExplainedCollectionCondition(this, explanation);
  }

  public abstract boolean missingElementSatisfiesCondition();

  /**
   * A temporary solution for keeping backward compatibility
   * until we throw away old method {@link #test(List)} and {@link #fail(CollectionSource, List, Exception, long)}
   */
  @Desugar
  private record ActualElementsHolder(List<WebElement> elements) {
    @Override
    public String toString() {
      return "Elements: " + elements;
    }
  }
}
