package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;

import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class ContainTexts extends CollectionCondition {
  private final List<String> expectedTexts;

  public ContainTexts(String... expectedTexts) {
    this(asList(expectedTexts));
  }

  public ContainTexts(List<String> expectedTexts) {
    if (expectedTexts.isEmpty()) {
      throw new IllegalArgumentException("No expected texts given");
    }
    this.expectedTexts = unmodifiableList(expectedTexts);
  }

  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.size() < expectedTexts.size()) {
      return false;
    }

    return ElementsCollection
      .texts(elements)
      .containsAll(expectedTexts);
  }

  @Override
  public void fail(CollectionSource collection,
                   @Nullable List<WebElement> elements,
                   @Nullable Exception lastError,
                   long timeoutMs) {
    if (elements == null || elements.isEmpty()) {
      ElementNotFound elementNotFound = new ElementNotFound(collection, toString(), lastError);
      elementNotFound.timeoutMs = timeoutMs;
      throw elementNotFound;
    } else if (elements.size() < expectedTexts.size()) {
      throw new TextsSizeMismatch(collection, ElementsCollection.texts(elements), expectedTexts, explanation, timeoutMs);
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
    return "Contains texts " + expectedTexts;
  }
}
