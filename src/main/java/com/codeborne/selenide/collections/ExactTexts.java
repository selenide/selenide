package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

@ParametersAreNonnullByDefault
public class ExactTexts extends CollectionCondition {
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

  @Nonnull
  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    List<String> actualTexts = communicator.texts(driver, elements);
    if (actualTexts.size() != expectedTexts.size()) {
      String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedTexts.size(), actualTexts.size());
      return rejected(message, actualTexts);
    }
    for (int i = 0; i < expectedTexts.size(); i++) {
      String expectedText = expectedTexts.get(i);
      String actualText = actualTexts.get(i);
      if (!check(actualText, expectedText)) {
        String message = String.format("Text #%s mismatch (expected: \"%s\", actual: \"%s\")", i, expectedText, actualText);
        return rejected(message, actualTexts);
      }
    }
    return CheckResult.accepted();
  }

  @CheckReturnValue
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
  public boolean missingElementSatisfiesCondition() {
    return false;
  }

  @Override
  public String toString() {
    return "Exact texts " + expectedTexts;
  }
}
