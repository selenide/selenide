package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
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

  @CheckReturnValue
  @Override
  public boolean test(List<WebElement> elements) {
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
  public void fail(WebElementsCollection collection,
                   @Nullable List<WebElement> elements,
                   @Nullable Exception lastError,
                   long timeoutMs) {
    if (elements == null || elements.isEmpty()) {
      ElementNotFound elementNotFound = new ElementNotFound(collection, toString(), lastError);
      elementNotFound.timeoutMs = timeoutMs;
      throw elementNotFound;
    }
    else if (elements.size() != expectedTexts.size()) {
      throw new TextsSizeMismatch(collection, ElementsCollection.texts(elements), expectedTexts, explanation, timeoutMs);
    }
    else {
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
