package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.AttributesMismatch;
import com.codeborne.selenide.ex.AttributesSizeMismatch;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
public class Attributes extends CollectionCondition {
  protected final List<String> expectedValues;
  protected final String attribute;

  public Attributes(String attribute, List<String> expectedValues) {
    if (expectedValues.isEmpty()) {
      throw new IllegalArgumentException("No expected values given");
    }
    this.expectedValues = unmodifiableList(expectedValues);
    this.attribute = attribute;
  }

  @CheckReturnValue
  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.size() != expectedValues.size()) {
      return false;
    }

    for (int i = 0; i < expectedValues.size(); i++) {
      WebElement element = elements.get(i);
      String expectedValue = expectedValues.get(i);
      if (!Objects.equals(element.getAttribute(attribute), expectedValue)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void fail(CollectionSource collection,
                   @Nullable List<WebElement> elements,
                   @Nullable Exception cause,
                   long timeoutMs) {
    if (elements == null || elements.isEmpty()) {
      throw new ElementNotFound(collection, toString(), timeoutMs, cause);
    } else if (elements.size() != expectedValues.size()) {
      throw new AttributesSizeMismatch(collection.driver(), attribute, collection, expectedValues,
        ElementsCollection.attributes(attribute, elements), explanation, timeoutMs);
    } else {
      throw new AttributesMismatch(collection.driver(), attribute, collection, expectedValues,
        ElementsCollection.attributes(attribute, elements), explanation, timeoutMs);
    }
  }

  @Override
  public boolean missingElementSatisfiesCondition() {
    return false;
  }

  @Override
  public String toString() {
    return "Attribute: '" + attribute + "' values " + expectedValues;
  }
}
