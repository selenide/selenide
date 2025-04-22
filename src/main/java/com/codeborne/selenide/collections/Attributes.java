package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.AttributesMismatch;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class Attributes extends WebElementsCondition {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

  protected final List<String> expectedValues;
  protected final String attribute;

  public Attributes(String attribute, List<String> expectedValues) {
    if (expectedValues.isEmpty()) {
      throw new IllegalArgumentException("No expected values given for attribute " + attribute);
    }
    this.expectedValues = unmodifiableList(expectedValues);
    this.attribute = attribute;
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    List<@Nullable String> actualValues = communicator.attributes(driver, elements, attribute);
    int expectedValuesSize = expectedValues.size();
    int actualValuesSize = actualValues.size();
    if (actualValuesSize != expectedValuesSize) {
      String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedValuesSize, actualValuesSize);
      return CheckResult.rejected(message, actualValues);
    }
    for (int i = 0; i < expectedValuesSize; i++) {
      String expectedValue = expectedValues.get(i);
      String actualValue = actualValues.get(i);

      if (!Objects.equals(actualValue, expectedValue)) {
        String message = String.format("Attribute \"%s\" values mismatch (#%s expected: \"%s\", actual: \"%s\")",
          attribute, i, expectedValue, actualValue);
        return CheckResult.rejected(message, actualValues);
      }
    }
    return CheckResult.accepted();
  }

  @Override
  public void fail(CollectionSource collection,
                   CheckResult lastCheckResult,
                   @Nullable Exception cause,
                   long timeoutMs) {
    List<String> actualAttributeValues = lastCheckResult.getActualValueOrElse(emptyList());

    if (actualAttributeValues.isEmpty()) {
      throw new ElementNotFound(collection, toString(), timeoutMs, cause);
    }

    String message = lastCheckResult.getMessageOrElse(() -> String.format("Attribute '%s' values mismatch", attribute));
    throw new AttributesMismatch(message, collection, expectedValues,
      actualAttributeValues, explanation, timeoutMs, cause);
  }

  @Override
  public String toString() {
    return "Attribute: '" + attribute + "' values " + expectedValues;
  }
}
