package com.codeborne.selenide;

import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.*;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.*;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.logevents.ErrorsCollector.validateAssertionMode;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.stream.Collectors.toList;

public class ElementsCollection extends AbstractList<SelenideElement> {
  private final WebElementsCollection collection;

  public ElementsCollection(WebElementsCollection collection) {
    this.collection = collection;
  }

  /**
   * Checks is the collection is of given size
   *
   * @param expectedSize
   * @return ElementsCollection
   */
  public ElementsCollection shouldHaveSize(int expectedSize) {
    return shouldHave(CollectionCondition.size(expectedSize));
  }

  /**
   * $$(".error").shouldBe(empty)
   */
  public ElementsCollection shouldBe(CollectionCondition... conditions) {
    return should("be", conditions);
  }

  /**
   * $$(".error").shouldHave(size(3))
   * $$(".error").shouldHave(texts("Error1", "Error2"))
   */
  public ElementsCollection shouldHave(CollectionCondition... conditions) {
    return should("have", conditions);
  }

  protected ElementsCollection should(String prefix, CollectionCondition... conditions) {
    validateAssertionMode();

    SelenideLog log = SelenideLogger.beginStep(collection.description(), "should " + prefix, (Object[]) conditions);
    try {
      for (CollectionCondition condition : conditions) {
        waitUntil(condition, collectionsTimeout);
      }
      SelenideLogger.commitStep(log, PASS);
      return this;
    }
    catch (Error error) {
      SelenideLogger.commitStep(log, error);
      switch (assertionMode) {
        case SOFT:
          return this;
        default:
          throw UIAssertionError.wrap(error, collectionsTimeout);
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
    final long startTime = System.currentTimeMillis();
    do {
      try {
        actualElements = collection.getActualElements();
        if (condition.apply(actualElements)) {
          return;
        }
      }
      catch (WebDriverException elementNotFound) {
        lastError = elementNotFound;

        if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
          throw Cleanup.of.wrap(elementNotFound);
        }
      }
      sleep(collectionsPollingInterval);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);
    condition.fail(collection, actualElements, lastError, timeoutMs);
  }
  void sleep(long ms) {
    Selenide.sleep(ms);
  }

  /**
   * Filters collection elements based on the given condition
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection filter(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, condition));
  }

  /**
   * Filters collection elements based on the given condition
   * @see #filter(Condition)
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection filterBy(Condition condition) {
    return filter(condition);
  }

  /**
   * Filters elements excluding those which met the given condition
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection exclude(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, not(condition)));
  }

  /**
   * Filters elements excluding those which met the given condition
   * @see #exclude(Condition)
   * @param condition
   * @return ElementsCollection
   */
  public ElementsCollection excludeWith(Condition condition) {
    return exclude(condition);
  }

  /**
   * Find the first element which met the given condition
   * @param condition
   * @return SelenideElement
   */
  public SelenideElement find(Condition condition) {
    return CollectionElementByCondition.wrap(collection, condition);
  }

  /**
   * Find the first element which met the given condition
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
   * @deprecated Use method com.codeborne.selenide.ElementsCollection#texts() that returns List instead of array
   */
  @Deprecated
  public String[] getTexts() {
    return getTexts(getElements());
  }

  /**
   * Fail-safe method for retrieving texts of given elements.
   * @param elements Any collection of WebElements
   * @return Array of texts (or exceptions in case of any WebDriverExceptions)
   */
  public static List<String> texts(Collection<WebElement> elements) {
    return elements.stream().map(e -> getText(e)).collect(toList());
  }

  /**
   * @deprecated Use method com.codeborne.selenide.ElementsCollection#texts(java.util.Collection)
   *              that returns List instead of array
   */
  @Deprecated
  public static String[] getTexts(Collection<WebElement> elements) {
    String[] texts = new String[elements.size()];
    int i = 0;
    for (WebElement element : elements) {
      texts[i++] = getText(element);
    }
    return texts;
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
  public static String elementsToString(Collection<WebElement> elements) {
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
      sb.append($(element));
    }
    sb.append("\n]");
    return sb.toString();
  }

  @Override
  public SelenideElement get(int index) {
    return CollectionElement.wrap(collection, index);
  }

  /**
   * return the first element of the collection
   * @return
   */
  public SelenideElement first() {
    return get(0);
  }

  /**
   * return the last element of the collection
   * @return
   */
  public SelenideElement last() {
    return get(size() - 1);
  }

  /**
   * returns the first n elements of the collection
   * @param elements - number of elements
   * @return
   */
  public ElementsCollection first(int elements) {
    List<WebElement> sublist = getElements().subList(0, Math.min(elements, size()));
    return new ElementsCollection(new WebElementsCollectionWrapper(sublist));
  }

  /**
   * returns the last n elements of the collection
   * @param elements - number of elements
   * @return
   */
  public ElementsCollection last(int elements) {
    List<WebElement> sublist = getElements().subList(Math.max(size() - elements, 0), size());
    return new ElementsCollection(new WebElementsCollectionWrapper(sublist));
  }

  @Override
  public int size() {
    return getElements().size();
  }

  @Override
  public Iterator<SelenideElement> iterator() {
    return new SelenideElementIterator(collection);
  }

  @Override
  public ListIterator<SelenideElement> listIterator(int index) {
    return new SelenideElementListIterator(collection, index);
  }

  @Override
  public String toString() {
    try {
      return elementsToString(getElements());
    } catch (Exception e) {
      return String.format("[%s]", Cleanup.of.webdriverExceptionMessage(e));
    }
  }
}
