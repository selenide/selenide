package com.codeborne.selenide;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.CollectionElement;
import com.codeborne.selenide.impl.CollectionElementByCondition;
import com.codeborne.selenide.impl.CollectionSnapshot;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import com.codeborne.selenide.impl.FilteringCollection;
import com.codeborne.selenide.impl.HeadOfCollection;
import com.codeborne.selenide.impl.LastCollectionElement;
import com.codeborne.selenide.impl.SelenideElementIterator;
import com.codeborne.selenide.impl.SelenideElementListIterator;
import com.codeborne.selenide.impl.TailOfCollection;
import com.codeborne.selenide.impl.WebElementsCollectionWrapper;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class ElementsCollection extends AbstractList<SelenideElement> {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

  private final CollectionSource collection;

  public ElementsCollection(CollectionSource collection) {
    this.collection = collection;
  }

  public ElementsCollection(Driver driver, Collection<? extends WebElement> elements) {
    this(new WebElementsCollectionWrapper(driver, elements));
  }

  public ElementsCollection(Driver driver, String cssSelector) {
    this(driver, By.cssSelector(cssSelector));
  }

  public ElementsCollection(Driver driver, By seleniumSelector) {
    this(new BySelectorCollection(driver, seleniumSelector));
  }

  /**
   * Check if a collection matches given condition(s).
   *
   * <p> For example: </p>
   *
   * <pre>
   * {@code
   * $$(".text_list").should(containExactTextsCaseSensitive("text1", "text2"));
   * $$(".cat_list").should(allMatch("value==cat", el -> el.getAttribute("value").equals("cat")));
   * }
   * </pre>
   */
  @Nonnull
  public ElementsCollection should(CollectionCondition... conditions) {
    return should("", Duration.ofMillis(driver().config().timeout()), conditions);
  }

  /**
   * Check if a collection matches a given condition within the given time period.
   *
   * @param timeout maximum waiting time
   */
  @Nonnull
  public ElementsCollection should(CollectionCondition condition, Duration timeout) {
    return should("", timeout, toArray(condition));
  }

  /**
   * For example: {@code $$(".error").shouldBe(empty)}
   */
  @Nonnull
  public ElementsCollection shouldBe(CollectionCondition... conditions) {
    return should("be", Duration.ofMillis(driver().config().timeout()), conditions);
  }

  @Nonnull
  public ElementsCollection shouldBe(CollectionCondition condition, Duration timeout) {
    return should("be", timeout, toArray(condition));
  }

  /**
   * For example:
   * {@code $$(".error").shouldHave(size(3))}
   * {@code $$(".error").shouldHave(texts("Error1", "Error2"))}
   */
  @Nonnull
  public ElementsCollection shouldHave(CollectionCondition... conditions) {
    return should("have", Duration.ofMillis(driver().config().timeout()), conditions);
  }

  /**
   * Check if a collection matches given condition within given period
   *
   * @param timeout maximum waiting time
   */
  @Nonnull
  public ElementsCollection shouldHave(CollectionCondition condition, Duration timeout) {
    return should("have", timeout, toArray(condition));
  }

  @CheckReturnValue
  @Nonnull
  private CollectionCondition[] toArray(CollectionCondition condition) {
    return new CollectionCondition[]{condition};
  }

  @Nonnull
  protected ElementsCollection should(String prefix, Duration timeout, CollectionCondition... conditions) {
    validateAssertionMode(driver().config());

    SelenideLog log = SelenideLogger.beginStep(collection.shortDescription(), "should " + prefix, (Object[]) conditions);
    try {
      for (CollectionCondition condition : conditions) {
        waitUntil(condition, timeout);
      }
      SelenideLogger.commitStep(log, PASS);
      return this;
    }
    catch (Error error) {
      Error wrappedError = UIAssertionError.wrap(driver(), error, timeout.toMillis());
      SelenideLogger.commitStep(log, wrappedError);
      switch (driver().config().assertionMode()) {
        case SOFT:
          return this;
        default:
          throw wrappedError;
      }
    }
    catch (RuntimeException e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  protected void waitUntil(CollectionCondition condition, Duration timeout) {
    Throwable lastError = null;
    CheckResult lastCheckResult = new CheckResult(REJECT, null);
    Stopwatch stopwatch = new Stopwatch(timeout.toMillis());
    do {
      try {
        lastCheckResult = condition.check(collection);
        if (lastCheckResult.verdict() == ACCEPT) {
          return;
        }
      }
      catch (JavascriptException e) {
        throw e;
      }
      catch (WebDriverException | IndexOutOfBoundsException | UIAssertionError elementNotFound) {
        if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
          throw Cleanup.of.wrapInvalidSelectorException(elementNotFound);
        }
        if (condition.missingElementSatisfiesCondition()) {
          return;
        }
        lastError = elementNotFound;
      }
      sleep(driver().config().pollingInterval());
    }
    while (!stopwatch.isTimeoutReached());

    if (lastError instanceof IndexOutOfBoundsException) {
      throw new ElementNotFound(collection.driver(), collection.getAlias(), collection.description(), exist, lastError);
    }
    else if (lastError instanceof UIAssertionError uiAssertionError) {
      throw uiAssertionError;
    }
    else {
      condition.fail(collection, lastCheckResult, (Exception) lastError, timeout.toMillis());
    }
  }

  void sleep(long ms) {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return ElementsCollection
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection filter(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, condition));
  }

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return ElementsCollection
   * @see #filter(Condition)
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection filterBy(Condition condition) {
    return filter(condition);
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return ElementsCollection
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection exclude(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, not(condition)));
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return ElementsCollection
   * @see #exclude(Condition)
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection excludeWith(Condition condition) {
    return exclude(condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return SelenideElement
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElement find(Condition condition) {
    return CollectionElementByCondition.wrap(collection, condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return SelenideElement
   * @see #find(Condition)
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElement findBy(Condition condition) {
    return find(condition);
  }

  @CheckReturnValue
  @Nonnull
  private List<WebElement> getElements() {
    return collection.getElements();
  }

  /**
   * Gets all the texts in elements collection
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * Instead of just getting texts, we highly recommend to verify them with {@code $$.shouldHave(texts(...));}.
   */
  @CheckReturnValue
  @Nonnull
  public List<String> texts() {
    return communicator.texts(driver(), getElements());
  }

  /**
   * Fail-safe method for retrieving texts of given elements.
   *
   * @param elements Any collection of WebElements
   * @return Texts (or exceptions in case of any WebDriverExceptions)
   * @deprecated Use method {@link ElementsCollection#texts()} instead (or even better, don't use it at all)
   */
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public static List<String> texts(@Nullable Collection<WebElement> elements) {
    return elements == null ? emptyList() : elements.stream().map(ElementsCollection::getText).collect(toList());
  }

  @Deprecated
  private static String getText(WebElement element) {
    try {
      return element.getText();
    }
    catch (WebDriverException elementDisappeared) {
      return elementDisappeared.toString();
    }
  }


  /**
   * Gets all the specific attribute values in elements collection
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * Instead of just getting attributes, we highly recommend to verify them with {@code $$.shouldHave(attributes(...));}.
   */
  @CheckReturnValue
  @Nonnull
  public List<String> attributes(String attribute) {
    return communicator.attributes(driver(), getElements(), attribute);
  }

  /**
   * Fail-safe method for retrieving specific attribute values of given elements.
   *
   * @param elements Any collection of WebElements
   * @return Texts (or exceptions in case of any WebDriverExceptions)
   * @deprecated Instead of getting attributes, verify them with {@code $$.shouldHave(attributes(...));}.
   */
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public static List<String> attributes(String attribute, Collection<WebElement> elements) {
    return elements.stream().map(e -> getAttribute(e, attribute)).collect(toList());
  }

  @Deprecated
  private static String getAttribute(WebElement element, String attribute) {
    try {
      return element.getAttribute(attribute);
    }
    catch (WebDriverException elementDisappeared) {
      return elementDisappeared.toString();
    }
  }

  /**
   * Gets the n-th element of collection (lazy evaluation)
   *
   * @param index 0..N
   * @return the n-th element of collection
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  @Override
  public SelenideElement get(int index) {
    return CollectionElement.wrap(collection, index);
  }

  /**
   * <p>
   * returns the first element of the collection (lazy evaluation)
   * </p>
   *
   * <p>
   * NOTICE: Instead of {@code $$(css).first()}, prefer {@code $(css)} as it's faster and returns the same result
   * </p>
   *
   * @return the first element of the collection
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElement first() {
    return get(0);
  }

  /**
   * returns the last element of the collection (lazy evaluation)
   *
   * @return the last element of the collection
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElement last() {
    return LastCollectionElement.wrap(collection);
  }

  /**
   * returns the first n elements of the collection (lazy evaluation)
   *
   * @param elements number of elements 1..N
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection first(int elements) {
    return new ElementsCollection(new HeadOfCollection(collection, elements));
  }

  /**
   * returns the last n elements of the collection (lazy evaluation)
   *
   * @param elements number of elements 1..N
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection last(int elements) {
    return new ElementsCollection(new TailOfCollection(collection, elements));
  }

  /**
   * <p>
   * return actual size of the collection, doesn't wait on collection to be loaded.
   * </p>
   *
   * @return actual size of the collection
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @CheckReturnValue
  @Override
  public int size() {
    try {
      return getElements().size();
    }
    catch (IndexOutOfBoundsException outOfCollection) {
      return 0;
    }
  }

  /**
   * Not recommended: As a rule, tests should not iterate collection elements.
   * Instead, try to write a {@link CollectionCondition} which verifies the whole collection.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * @deprecated Use either {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @Deprecated
  public Stream<SelenideElement> stream() {
    return super.stream();
  }

  /**
   * Not recommended: As a rule, tests should not iterate collection elements.
   * Instead, try to write a {@link CollectionCondition} which verifies the whole collection.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * @deprecated Use either {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @Deprecated
  public Stream<SelenideElement> parallelStream() {
    return super.parallelStream();
  }

  /**
   * Not recommended: As a rule, tests should not iterate collection elements.
   * Instead, try to write a {@link CollectionCondition} which verifies the whole collection.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * @deprecated Use either {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @Deprecated
  public void forEach(Consumer<? super SelenideElement> action) {
    super.forEach(action);
  }

  /**
   * Does not reload collection elements while iterating it.
   *
   * Not recommended: As a rule, tests should not iterate collection elements.
   * Instead, try to write a {@link CollectionCondition} which verifies the whole collection.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * @deprecated use method {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public Iterator<SelenideElement> iterator() {
    return asFixedIterable().iterator();
  }

  /**
   * Does not reload collection elements while iterating it.
   *
   * Not recommended: As a rule, tests should not iterate collection elements.
   * Instead, try to write a {@link CollectionCondition} which verifies the whole collection.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * @deprecated use method {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public ListIterator<SelenideElement> listIterator(int index) {
    return new SelenideElementListIterator(new CollectionSnapshot(collection), index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public ListIterator<SelenideElement> listIterator() {
    return listIterator(0);
  }

  @Override
  @Deprecated
  protected void removeRange(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException("You cannot remove elements from web page");
  }

  @Override
  @Deprecated
  public void clear() {
    throw new UnsupportedOperationException("You cannot remove elements from web page");
  }

  @Override
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public List<SelenideElement> subList(int fromIndex, int toIndex) {
    return super.subList(fromIndex, toIndex);
  }

  /**
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * @deprecated use method {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public Object[] toArray() {
    List<WebElement> fetchedElements = collection.getElements();
    Object[] result = new Object[fetchedElements.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = CollectionElement.wrap(collection, i);
    }
    return result;
  }

  /**
   * Takes the snapshot of current state of this collection.
   * Succeeding calls to this object WILL NOT RELOAD collection element from browser.
   * <p>
   * Use it to speed up your tests - but only if you know that collection will not be changed during the test.
   *
   * @return current state of this collection
   * @see #asFixedIterable()
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection snapshot() {
    return new ElementsCollection(new CollectionSnapshot(collection));
  }

  /**
   * Returns a "static" {@link Iterable} which doesn't reload web elements during iteration.
   *
   * It's faster than {@link #asDynamicIterable()} ()},
   * but can sometimes can cause {@link org.openqa.selenium.StaleElementReferenceException} etc.
   * if elements are re-rendered during the iteration.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * @since 6.2.0
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElementIterable asFixedIterable() {
    return () -> new SelenideElementIterator(new CollectionSnapshot(collection));
  }

  /**
   * Returns a "dynamic" {@link Iterable} which reloads web elements during iteration.
   *
   * It's slower than {@link #asFixedIterable()}, but helps to avoid {@link org.openqa.selenium.StaleElementReferenceException} etc.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * @since 6.2.0
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElementIterable asDynamicIterable() {
    return () -> new SelenideElementIterator(collection);
  }

  /**
   * Give this collection a human-readable name
   * <p>
   * Caution: you probably don't need this method.
   * It's always a good idea to have the actual selector instead of "nice" description (which might be misleading or even lying).
   *
   * @param alias a human-readable name of this collection (null or empty string not allowed)
   * @return this collection
   * @since 5.20.0
   */
  @CheckReturnValue
  @Nonnull
  public ElementsCollection as(String alias) {
    this.collection.setAlias(alias);
    return this;
  }

  @Override
  @CheckReturnValue
  public String toString() {
    return collection.getAlias().getOrElse(collection::toString);
  }

  @CheckReturnValue
  @Nonnull
  private Driver driver() {
    return collection.driver();
  }

  public interface SelenideElementIterable extends Iterable<SelenideElement> {
    default Stream<SelenideElement> stream() {
      return StreamSupport.stream(spliterator(), false);
    }
  }
}
