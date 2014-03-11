package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ExactTexts extends CollectionCondition {
  protected final String[] expectedTexts;

  public ExactTexts(final String... expectedTexts) {
    if (expectedTexts.length == 0) {
      throw new IllegalArgumentException("Array of expected texts is empty");
    }
    this.expectedTexts = expectedTexts;
  }

  @Override
  public boolean apply(List<WebElement> elements) {
    if (elements.size() != expectedTexts.length) {
      return false;
    }

    for (int i = 0; i < expectedTexts.length; i++) {
      WebElement element = elements.get(i);
      String expectedText = expectedTexts[i];
      if (!Html.text.equals(element.getText(), expectedText)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
    if (elements == null || elements.isEmpty()) {
      throw new ElementNotFound(collection, expectedTexts, lastError, timeoutMs);
    } else {
      throw new TextsMismatch(collection, ElementsCollection.getTexts(elements), expectedTexts, timeoutMs);
    }
  }
}
