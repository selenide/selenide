package com.codeborne.selenide;

import com.codeborne.selenide.impl.*;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.*;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.pollingInterval;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.sleep;

public class ElementsCollection extends AbstractList<SelenideElement> {
  private final WebElementsCollection collection;
  private List<WebElement> actualElements;

  public ElementsCollection(WebElementsCollection collection) {
    this.collection = collection;
  }

  public void shouldHaveSize(int expectedSize) {
    shouldHave(CollectionCondition.size(expectedSize));
  }

  /**
   * $$(".error").shouldBe(empty)
   */
  public void shouldBe(CollectionCondition condition) {
    shouldHave(condition);
  }

  /**
   * $$(".error").shouldHave(size(3))
   * $$(".error").shouldHave(texts("Error1", "Error2"))
   */
  public void shouldHave(CollectionCondition condition) {
    final long startTime = System.currentTimeMillis();
    do {
      try {
        actualElements = collection.getActualElements();
        if (condition.apply(actualElements)) {
          return;
        }
      } catch (WebDriverException ignore) {
      }
      sleep(pollingInterval);
    }
    while (System.currentTimeMillis() - startTime < timeout);
    condition.fail(actualElements);
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

  public static String[] getTexts(Collection<WebElement> elements) {
    String[] texts = new String[elements.size()];
    int i = 0;
    for (WebElement element : elements) {
      texts[i++] = element.getText();
    }
    return texts;
  }

  @Override
  public SelenideElement get(int index) {
    return WebElementProxy.wrap(getActualElements().get(index));
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
}
