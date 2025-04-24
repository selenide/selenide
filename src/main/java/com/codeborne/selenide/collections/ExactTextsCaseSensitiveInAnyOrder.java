package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementCommunicator;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.impl.Plugins.inject;

public class ExactTextsCaseSensitiveInAnyOrder extends ExactTexts {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

  public ExactTextsCaseSensitiveInAnyOrder(String... exactTexts) {
    super(exactTexts);
  }

  public ExactTextsCaseSensitiveInAnyOrder(List<String> exactTexts) {
    super(exactTexts);
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    List<String> actualTexts = communicator.texts(driver, elements);
    int expectedTextSize = expectedTexts.size();
    int actualTextSize = actualTexts.size();
    if (actualTextSize != expectedTextSize) {
      String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedTextSize, actualTextSize);
      return rejected(message, actualTexts);
    }

    for (int i = 0; i < expectedTextSize; i++) {
      String expectedText = expectedTexts.get(i);
      if (!find(actualTexts, expectedText)) {
        String message = String.format("Text #%s not found: \"%s\"", i, expectedText);
        return CheckResult.rejected(message, actualTexts);
      }
    }
    return CheckResult.accepted();
  }

  private boolean find(List<String> texts, String text) {
    for (String actualText : texts) {
      if (Html.text.equalsCaseSensitive(actualText, text)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Exact texts case sensitive in any order " + expectedTexts;
  }
}
