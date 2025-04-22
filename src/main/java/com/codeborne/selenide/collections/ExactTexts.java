package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import com.codeborne.selenide.impl.Html;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class ExactTexts extends WebElementsCondition {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

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
  public CheckResult check(Driver driver, List<WebElement> elements) {
    List<String> actualTexts = communicator.texts(driver, elements);
    int expectedValuesSize = expectedTexts.size();
    int actualValuesSize = actualTexts.size();
    if (actualValuesSize != expectedValuesSize) {
      String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedValuesSize, actualValuesSize);
      return rejected(message, actualTexts);
    }
    for (int i = 0; i < expectedValuesSize; i++) {
      String expectedText = expectedTexts.get(i);
      String actualText = actualTexts.get(i);
      if (!check(actualText, expectedText)) {
        String message = String.format("Text #%s mismatch (expected: \"%s\", actual: \"%s\")", i, expectedText, actualText);
        return rejected(message, actualTexts);
      }
    }
    return CheckResult.accepted();
  }

  protected boolean check(String actualText, String expectedText) {
    return Html.text.equals(actualText, expectedText);
  }

  @Override
  public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
    List<String> actualTexts = lastCheckResult.getActualValue();
    if (actualTexts == null || actualTexts.isEmpty()) {
      throw new ElementNotFound(collection, toString(), timeoutMs, cause);
    }
    else {
      String message = lastCheckResult.getMessageOrElse(() -> "Texts mismatch");
      throw new TextsMismatch(message, collection, expectedTexts, actualTexts, explanation, timeoutMs, cause);
    }
  }

  @Override
  public String toString() {
    return "Exact texts " + expectedTexts;
  }
}
