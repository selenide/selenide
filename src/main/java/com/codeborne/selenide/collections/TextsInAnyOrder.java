package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementCommunicator;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class TextsInAnyOrder extends ExactTexts {
  private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

  public TextsInAnyOrder(String... expectedTexts) {
    super(expectedTexts);
  }

  public TextsInAnyOrder(List<String> expectedTexts) {
    super(expectedTexts);
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
      boolean found = find(actualTexts, expectedText);
      if (!found) {
        String message = String.format("Text #%s not found: \"%s\"", i, expectedText);
        return CheckResult.rejected(message, actualTexts);
      }
    }
    return CheckResult.accepted();
  }

  private boolean find(List<String> texts, String text) {
    for (String elementText : texts) {
      if (Html.text.contains(elementText, text)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "TextsInAnyOrder " + expectedTexts;
  }
}
