package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class Attributes extends ExactAttributes {
  public Attributes(String attribute, String... expectedValues) {
    super(attribute, expectedValues);
  }

  public Attributes(String attribute, List<String> expectedValues) {
    super(attribute, expectedValues);
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
      if (!element.getAttribute(attribute).contains(expectedValue)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "Attribute: '"+attribute+"' values " + expectedValues;
  }
}
