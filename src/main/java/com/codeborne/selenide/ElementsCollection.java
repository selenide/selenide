package com.codeborne.selenide;

import com.codeborne.selenide.impl.*;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.*;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class ElementsCollection extends AbstractList<SelenideElement> {
  private final WebElementsCollection collection;
  private List<WebElement> actualElements;
  private Exception lastError;

  public ElementsCollection(WebElementsCollection collection) {
    this.collection = collection;
  }

  public ElementsCollection shouldHaveSize(int expectedSize) {
    return shouldHave(CollectionCondition.size(expectedSize));
  }

  /**
   * $$(".error").shouldBe(empty)
   */
  public ElementsCollection shouldBe(CollectionCondition... conditions) {
    return shouldHave(conditions);
  }

  /**
   * $$(".error").shouldHave(size(3))
   * $$(".error").shouldHave(texts("Error1", "Error2"))
   */
  public ElementsCollection shouldHave(CollectionCondition... conditions) {
    for (CollectionCondition condition : conditions) {
      waitUntil(condition, timeout);
    }
    return this;
  }

  protected void waitUntil(CollectionCondition condition, long timeoutMs) {
    lastError = null;
    final long startTime = System.currentTimeMillis();
    do {
      try {
        actualElements = collection.getActualElements();
        if (condition.apply(actualElements)) {
          return;
        }
      } catch (WebDriverException elementNotFound) {
        lastError = elementNotFound;

        if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
          throw Cleanup.of.wrap(elementNotFound);
        }
      }
      sleep(pollingInterval);
    }
    while (System.currentTimeMillis() - startTime < timeoutMs);

    condition.fail(collection, actualElements, lastError, timeoutMs);
  }

  public ElementsCollection filter(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, condition));
  }

  public ElementsCollection filterBy(Condition condition) {
    return filter(condition);
  }

  public ElementsCollection exclude(Condition condition) {
    return new ElementsCollection(new FilteringCollection(collection, not(condition)));
  }

  public ElementsCollection excludeWith(Condition condition) {
    return exclude(condition);
  }

  public SelenideElement find(Condition condition) {
    return filter(condition).get(0);
  }

  public SelenideElement findBy(Condition condition) {
    return find(condition);
  }

  private List<WebElement> getActualElements() {
    if (actualElements == null) {
      actualElements = collection.getActualElements();
    }
    return actualElements;
  }

  public String[] getTexts() {
    return getTexts(getActualElements());
  }

  /**
   * Fail-safe method for retrieving texts of given elements.
   * @param elements Any collection of WebElements
   * @return Array of texts (or exceptions in case of any WebDriverExceptions)
   */
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

  public static String elementsToString(Collection<WebElement> elements) {
    if (elements == null || elements.isEmpty()) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder(256);
    sb.append("[\n\t\t");
    for (WebElement element : elements) {
      if (sb.length() > 4) {
        sb.append(",\n\t\t");
      }
      sb.append($(element).toString());
    }
    sb.append("\n]");
    return sb.toString();
  }

  @Override
  public SelenideElement get(int index) {
    return CollectionElement.wrap(collection, index);
  }

  @Override
  public int size() {
    return getActualElements().size();
  }

  @Override
  public Iterator<SelenideElement> iterator() {
    return new SelenideElementIterator(getActualElements().iterator());
  }

  @Override
  public ListIterator<SelenideElement> listIterator(int index) {
    return new SelenideElementListIterator(getActualElements().listIterator(index));
  }

  @Override
  public String toString() {
    return elementsToString(actualElements);
  }
}
