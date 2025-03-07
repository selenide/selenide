package com.codeborne.selenide.appium.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.collections.ExactTexts;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.rejected;

public class AppiumAttributeValues extends ExactTexts {

  protected final CombinedAttribute attribute;

  public AppiumAttributeValues(CombinedAttribute attribute, String... expectedAttributeValues) {
    super(expectedAttributeValues);
    this.attribute = attribute;
  }

  public AppiumAttributeValues(CombinedAttribute attribute, List<String> expectedAttributeValues) {
    super(expectedAttributeValues);
    this.attribute = attribute;
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    List<@Nullable String> actualValues = getActualAttributes(driver, elements);
    if (actualValues.size() != expectedTexts.size()) {
      String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedTexts.size(), actualValues.size());
      return rejected(message, actualValues);
    }

    for (int i = 0; i < this.expectedTexts.size(); ++i) {
      String expectedAttributeValue = this.expectedTexts.get(i);
      String actualAttributeValue = actualValues.get(i);
      if (!Objects.equals(expectedAttributeValue, actualAttributeValue)) {
        String message = String.format("Attribute #%s mismatch (expected: \"%s\", actual: \"%s\")",
          i, expectedAttributeValue, actualAttributeValue);
        return CheckResult.rejected(message, actualValues);
      }
    }

    return new CheckResult(ACCEPT, null);
  }

  private List<@Nullable String> getActualAttributes(Driver driver, List<WebElement> elements) {
    return elements.stream()
      .map(element -> attribute.getAttributeValue(driver, element))
      .toList();
  }

  @Override
  public String toString() {
    return "%s %s".formatted(attribute, expectedTexts);
  }
}
