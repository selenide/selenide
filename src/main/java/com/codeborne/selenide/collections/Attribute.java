package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ex.CollectionNotAllElementsHaveAttributeWithValue;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class Attribute extends CollectionCondition {

  private final String attributeName;
  private final String attributeValue;

  public Attribute(String attributeName, String attributeValue) {
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }

  @Override
  public void fail(
    CollectionSource collection,
    @Nullable List<WebElement> elements,
    @Nullable Exception lastError,
    long timeoutMs
  ) {
    if (elements == null || elements.isEmpty()) {
      ElementNotFound elementNotFound = new ElementNotFound(collection, toString(), lastError);
      elementNotFound.timeoutMs = timeoutMs;
      throw elementNotFound;
    } else {
      throw new CollectionNotAllElementsHaveAttributeWithValue(
        collection.driver(),
        "Not all elements have attribute " + attributeName + " with value " + attributeValue,
        lastError
      );
    }

  }

  @Override
  public boolean missingElementSatisfiesCondition() {
    return false;
  }

  @Override
  public boolean test(List<WebElement> webElements) {
    return webElements.stream().allMatch(el -> el.getAttribute(attributeName).equals(attributeValue));
  }
}
