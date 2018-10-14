package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class ExactTexts extends CollectionCondition {
  protected final List<String> expectedTexts;

  public ExactTexts(String... expectedTexts) {
    this(asList(expectedTexts));
  }

  public ExactTexts(List<String> expectedTexts) {
    if (expectedTexts.isEmpty()) {
      throw new IllegalArgumentException("No expected texts given");
    }
    this.expectedTexts = unmodifiableList(expectedTexts);
  }

  @Override
  public boolean apply(List<WebElement> elements) {
    if (elements.size() != expectedTexts.size()) {
      return false;
    }

    for (int i = 0; i < expectedTexts.size(); i++) {
      WebElement element = elements.get(i);
      String expectedText = expectedTexts.get(i);
      if (!Html.text.equals(element.getText(), expectedText)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
    if (elements == null || elements.isEmpty()) {
      ElementNotFound elementNotFound = new ElementNotFound(collection, expectedTexts, lastError);
      elementNotFound.timeoutMs = timeoutMs;
      throw elementNotFound;
    } else {
      throw new TextsMismatch(collection, ElementsCollection.texts(elements), expectedTexts, explanation, timeoutMs);
    }
  }

  @Override
  public boolean applyNull() {
    return false;
  }

  @Override
  public String toString() {
    return "Exact texts " + expectedTexts;
  }
}
