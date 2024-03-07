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
import com.codeborne.selenide.impl.TailOfCollection;
import com.codeborne.selenide.impl.WebElementsCollectionWrapper;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.commands.Util.classOf;
import static com.codeborne.selenide.impl.Plugins.inject;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

@ParametersAreNonnullByDefault
public abstract class BaseElementsCollection<T extends SelenideElement, SELF extends BaseElementsCollection<T, SELF>>
  implements Iterable<T> {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

  private final Class<T> clazz;
  private final CollectionSource collection;

  @SafeVarargs
  protected BaseElementsCollection(CollectionSource collection, T... clazz) {
    this.clazz = classOf(clazz);
    this.collection = collection;
  }

  @SafeVarargs
  protected BaseElementsCollection(Driver driver, Collection<? extends WebElement> elements, T... clazz) {
    this(new WebElementsCollectionWrapper(driver, elements), clazz);
  }

  @SafeVarargs
  protected BaseElementsCollection(Driver driver, String cssSelector, T... clazz) {
    this(driver, By.cssSelector(cssSelector), clazz);
  }

  @SafeVarargs
  protected BaseElementsCollection(Driver driver, By seleniumSelector, T... clazz) {
    this(new BySelectorCollection(driver, seleniumSelector), clazz);
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
  public SELF should(WebElementsCondition... conditions) {
    return should("", Duration.ofMillis(driver().config().timeout()), conditions);
  }

  /**
   * Check if a collection matches a given condition within the given time period.
   *
   * @param timeout maximum waiting time
   */
  @Nonnull
  public SELF should(WebElementsCondition condition, Duration timeout) {
    return should("", timeout, toArray(condition));
  }

  /**
   * For example: {@code $$(".error").shouldBe(empty)}
   */
  @Nonnull
  public SELF shouldBe(WebElementsCondition... conditions) {
    return should("be", Duration.ofMillis(driver().config().timeout()), conditions);
  }

  @Nonnull
  public SELF shouldBe(WebElementsCondition condition, Duration timeout) {
    return should("be", timeout, toArray(condition));
  }

  /**
   * For example:
   * {@code $$(".error").shouldHave(size(3))}
   * {@code $$(".error").shouldHave(texts("Error1", "Error2"))}
   */
  @Nonnull
  public SELF shouldHave(WebElementsCondition... conditions) {
    return should("have", Duration.ofMillis(driver().config().timeout()), conditions);
  }

  /**
   * Check if a collection matches given condition within given period
   *
   * @param timeout maximum waiting time
   */
  @Nonnull
  public SELF shouldHave(WebElementsCondition condition, Duration timeout) {
    return should("have", timeout, toArray(condition));
  }

  @CheckReturnValue
  @Nonnull
  private WebElementsCondition[] toArray(WebElementsCondition condition) {
    return new WebElementsCondition[]{condition};
  }

  @Nonnull
  @SuppressWarnings("ErrorNotRethrown")
  protected SELF should(String prefix, Duration timeout, WebElementsCondition... conditions) {
    validateAssertionMode(driver().config());

    SelenideLog log = SelenideLogger.beginStep(collection.shortDescription(), "should " + prefix, (Object[]) conditions);
    try {
      for (WebElementsCondition condition : conditions) {
        waitUntil(condition, timeout);
      }
      SelenideLogger.commitStep(log, PASS);
      return self();
    }
    catch (Error error) {
      Error wrappedError = UIAssertionError.wrap(driver(), error, timeout.toMillis());
      SelenideLogger.commitStep(log, wrappedError);
      return switch (driver().config().assertionMode()) {
        case SOFT -> self();
        default -> throw wrappedError;
      };
    }
    catch (RuntimeException e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  private SELF self() {
    return (SELF) this;
  }

  @SuppressWarnings("ErrorNotRethrown")
  protected void waitUntil(WebElementsCondition condition, Duration timeout) {
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
      catch (JavascriptException | UnsupportedCommandException e) {
        throw e;
      }
      catch (WebDriverException | IndexOutOfBoundsException | UIAssertionError elementNotFound) {
        if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
          throw Cleanup.of.wrapInvalidSelectorException(elementNotFound);
        }
        if (condition.missingElementsSatisfyCondition()) {
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
  public SELF filter(WebElementCondition condition) {
    return create(new FilteringCollection(collection, condition));
  }

  protected abstract SELF create(CollectionSource source);

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return ElementsCollection
   * @see #filter(WebElementCondition)
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SELF filterBy(WebElementCondition condition) {
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
  public SELF exclude(WebElementCondition condition) {
    return create(new FilteringCollection(collection, not(condition)));
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return ElementsCollection
   * @see #exclude(WebElementCondition)
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SELF excludeWith(WebElementCondition condition) {
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
  public SelenideElement find(WebElementCondition condition) {
    return CollectionElementByCondition.wrap(collection, condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   *
   * @param condition condition
   * @return SelenideElement
   * @see #find(WebElementCondition)
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElement findBy(WebElementCondition condition) {
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
   * Gets the n-th element of collection (lazy evaluation)
   *
   * @param index 0..N
   * @return the n-th element of collection
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public T get(int index) {
    return CollectionElement.wrap(clazz, collection, index);
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
   * @param elements number of elements 1…N
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SELF first(int elements) {
    return create(new HeadOfCollection(collection, elements));
  }

  /**
   * returns the last n elements of the collection (lazy evaluation)
   *
   * @param elements number of elements 1…N
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  @CheckReturnValue
  @Nonnull
  public SELF last(int elements) {
    return create(new TailOfCollection(collection, elements));
  }

  /**
   * <p>
   * return actual size of the collection, doesn't wait until collection is fully loaded.
   * </p>
   *
   * @return actual size of the collection
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @CheckReturnValue
  public int size() {
    try {
      return getElements().size();
    }
    catch (IndexOutOfBoundsException outOfCollection) {
      return 0;
    }
  }

  /**
   * <p>
   * return actual state of the collection, doesn't wait until collection is fully loaded.
   * </p>
   *
   * @return actual size of the collection
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  public boolean isEmpty() {
    return size() == 0;
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
  public SELF snapshot() {
    return create(new CollectionSnapshot(collection));
  }

  /**
   * Does not reload collection elements while iterating it.
   *
   * Not recommended: As a rule, tests should not iterate collection elements.
   * Instead, try to write a {@link CollectionCondition} which verifies the whole collection.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * To make it explicit, we recommend to use method {@link #asFixedIterable()} or {@link #asDynamicIterable()} instead.
   */
  @Override
  @CheckReturnValue
  @Nonnull
  public Iterator<T> iterator() {
    return asFixedIterable().iterator();
  }

  /**
   * Returns a "static" {@link Iterable} which doesn't reload web elements during iteration.
   *
   * It's faster than {@link #asDynamicIterable()}},
   * but can sometimes can cause {@link org.openqa.selenium.StaleElementReferenceException} etc.
   * if elements are re-rendered during the iteration.
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * @since 6.2.0
   */
  @CheckReturnValue
  @Nonnull
  public SelenideElementIterable<T> asFixedIterable() {
    return () -> new SelenideElementIterator<>(new CollectionSnapshot(collection));
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
  public SelenideElementIterable<T> asDynamicIterable() {
    return () -> new SelenideElementIterator<>(collection);
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
  public SELF as(String alias) {
    this.collection.setAlias(alias);
    return self();
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

  @FunctionalInterface
  public interface SelenideElementIterable<T extends SelenideElement> extends Iterable<T> {
    default Stream<T> stream() {
      return StreamSupport.stream(spliterator(), false);
    }
  }
}
