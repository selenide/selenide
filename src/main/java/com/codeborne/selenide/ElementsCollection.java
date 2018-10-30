package com.codeborne.selenide;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.CollectionElement;
import com.codeborne.selenide.impl.CollectionElementByCondition;
import com.codeborne.selenide.impl.Describe;
import com.codeborne.selenide.impl.FilteringCollection;
import com.codeborne.selenide.impl.HeadOfCollection;
import com.codeborne.selenide.impl.LastCollectionElement;
import com.codeborne.selenide.impl.SelenideElementIterator;
import com.codeborne.selenide.impl.SelenideElementListIterator;
import com.codeborne.selenide.impl.TailOfCollection;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.codeborne.selenide.impl.WebElementsCollectionWrapper;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.stream.Collectors.toList;

public class ElementsCollection extends AbstractList<SelenideElement> {
  private final WebElementsCollection collection;

  public ElementsCollection(WebElementsCollection collection) {
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

  public ElementsCollection(Driver driver, WebElement parent, String cssSelector) {
    this(driver, parent, By.cssSelector(cssSelector));
  }

  public ElementsCollection(Driver driver, WebElement parent, By seleniumSelector) {
    this(new BySelectorCollection(driver, parent, seleniumSelector));
  }

  /**
   * Deprecated. Use {@code $$.shouldHave(size(expectedSize))} instead.
   */
  public ElementsCollection shouldHaveSize(int expectedSize) {
    return shouldHave(CollectionCondition.size(expectedSize));
  }

  /**
   * For example: {@code $$(".error").shouldBe(empty)}
   */
  public ElementsCollection shouldBe(CollectionCondition... conditions) {
    return should("be", driver().config().timeout(), conditions);
  }

  public ElementsCollection shouldBe(CollectionCondition condition, long timeoutMs) {
    return should("be", timeoutMs, toArray(condition));
  }

  /**
   * For example:
   * {@code $$(".error").shouldHave(size(3))}
   * {@code $$(".error").shouldHave(texts("Error1", "Error2"))}
   */
  public ElementsCollection shouldHave(CollectionCondition... conditions) {
    return should("have", driver().config().timeout(), conditions);
  }

  /**
   * Check if a collection matches given condition within given period
   *
   * @param timeoutMs maximum waiting time in milliseconds
   */
  public ElementsCollection shouldHave(CollectionCondition condition, long timeoutMs) {
    return should("have", timeoutMs, toArray(condition));
  }

  private CollectionCondition[] toArray(CollectionCondition condition) {
    return new CollectionCondition[]{condition};
  }

  protected ElementsCollection should(String prefix, long timeoutMs, CollectionCondition... conditions) {
    validateAssertionMode(driver().config());

    SelenideLog log = SelenideLogger.beginStep(collection.description(), "should " + prefix, (Object[]) conditions);
    try {
      for (CollectionCondition condition : conditions) {
        waitUntil(condition, timeoutMs);
      }
      SelenideLogger.commitStep(log, PASS);
      return this;
    }
    catch (Error error) {
      Error wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs);
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

  protected void waitUntil(CollectionCondition condition, long timeoutMs) {
    Exception lastError = null;
    List<WebElement> actualElements = null;
    Stopwatch stopwatch = new Stopwatch(timeoutMs);
    do {
      try {
        actualElements = collection.getElements();
        if (condition.apply(actualElements)) {
          return;
        }
      }
      catch (JavascriptException e) {
        throw e;
      }
      catch (WebDriverException elementNotFound) {
        lastError = elementNotFound;

        if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
          throw Cleanup.of.wrap(elementNotFound);
        }
      }
      catch (IndexOutOfBoundsException outOfCollection) {
        if (condition.applyNull()) {
          return;
        }

        throw outOfCollection;
      }
      sleep(driver().config().pollingInterval());
    }
    while (!stopwatch.isTimeoutReached());
    condition.fail(collection, actualElements, lastError, timeoutMs);
  }

  void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection filter(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, condition));
  }

  /**
   * Filters collection elements based on the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @see #filter(Condition)
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection filterBy(Condition condition) {
    return filter(condition);
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection exclude(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, not(condition)));
  }

  /**
   * Filters elements excluding those which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @see #exclude(Condition)
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection excludeWith(Condition condition) {
    return exclude(condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @param condition
   * @return SelenideElement
   */
  public SelenideElement find(Condition condition) {
    return CollectionElementByCondition.wrap(collection, condition);
  }

  /**
   * Find the first element which met the given condition (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
   * @see #find(Condition)
   * @param condition
   * @return SelenideElement
   */
  public SelenideElement findBy(Condition condition) {
    return find(condition);
  }

  private List<WebElement> getElements() {
    return collection.getElements();
  }

  /**
   * Gets all the texts in elements collection
   * @return array of texts
   */
  public List<String> texts() {
    return texts(getElements());
  }

  /**
   * Fail-safe method for retrieving texts of given elements.
   * @param elements Any collection of WebElements
   * @return Array of texts (or exceptions in case of any WebDriverExceptions)
   */
  public static List<String> texts(Collection<WebElement> elements) {
    return elements.stream().map(ElementsCollection::getText).collect(toList());
  }

  private static String getText(WebElement element) {
    try {
      return element.getText();
    } catch (WebDriverException elementDisappeared) {
      return elementDisappeared.toString();
    }
  }

  /**
   * Outputs string presentation of the element's collection
   * @param elements
   * @return String
   */
  public static String elementsToString(Driver driver, Collection<WebElement> elements) {
    if (elements == null) {
      return "[not loaded yet...]";
    }

    if (elements.isEmpty()) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder(256);
    sb.append("[\n\t");
    for (WebElement element : elements) {
      if (sb.length() > 4) {
        sb.append(",\n\t");
      }
      sb.append(Describe.describe(driver, element));
    }
    sb.append("\n]");
    return sb.toString();
  }

  /**
   * Gets the n-th element of collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   *
   * @param index 0..N
   * @return
   */
  @Override
  public SelenideElement get(int index) {
    return CollectionElement.wrap(collection, index);
  }

  /**
   * returns the first element of the collection
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * NOTICE: $(css) is faster and returns the same result as $$(css).first()
   * @return
   */
  public SelenideElement first() {
    return get(0);
  }

  /**
   * returns the last element of the collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * @return
   */
  public SelenideElement last() {
    return LastCollectionElement.wrap(collection);
  }

  /**
   * returns the first n elements of the collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * @param elements number of elements 1..N
   */
  public ElementsCollection first(int elements) {
    return new ElementsCollection(new HeadOfCollection(collection, elements));
  }

  /**
   * returns the last n elements of the collection (lazy evaluation)
   * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
   * @param elements number of elements 1..N
   */
  public ElementsCollection last(int elements) {
    return new ElementsCollection(new TailOfCollection(collection, elements));
  }

  /**
   * return actual size of the collection, doesn't wait on collection to be loaded.
   * ATTENTION not recommended for use in tests. Use collection.shouldHave(size(n)); for assertions instead.
   * @return
   */
  @Override
  public int size() {
    try {
      return getElements().size();
    } catch (IndexOutOfBoundsException outOfCollection) {
      return 0;
    }
  }

  @Override
  public Iterator<SelenideElement> iterator() {
    return new SelenideElementIterator(fetch());
  }

  @Override
  public ListIterator<SelenideElement> listIterator(int index) {
    return new SelenideElementListIterator(fetch(), index);
  }

  private WebElementsCollectionWrapper fetch() {
    List<WebElement> fetchedElements = collection.getElements();
    return new WebElementsCollectionWrapper(driver(), fetchedElements);
  }

  @Override
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
   *
   * Use it to speed up your tests - but only if you know that collection will not be changed during the test.
   *
   * @return current state of this collection
   */
  public ElementsCollection snapshot() {
    return new ElementsCollection(fetch());
  }

  @Override
  public String toString() {
    try {
      return elementsToString(driver(), getElements());
    } catch (Exception e) {
      return String.format("[%s]", Cleanup.of.webdriverExceptionMessage(e));
    }
  }

  private Driver driver() {
    return collection.driver();
  }
}
