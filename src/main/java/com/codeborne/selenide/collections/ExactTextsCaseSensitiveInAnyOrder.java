package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ParametersAreNonnullByDefault
public class ExactTextsCaseSensitiveInAnyOrder extends ExactTexts {

  public ExactTextsCaseSensitiveInAnyOrder(String... exactTexts) {
    super(exactTexts);
  }

  public ExactTextsCaseSensitiveInAnyOrder(List<String> exactTexts) {
    super(exactTexts);
  }

  @CheckReturnValue
  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.size() != expectedTexts.size()) {
      return false;
    }

    List<String> elementsTexts = elements.stream().map(WebElement::getText).collect(toList());

    for (String expectedText : expectedTexts) {
      boolean found = false;
      for (String elementText : elementsTexts) {
        if (Html.text.equalsCaseSensitive(elementText, expectedText)) {
          found = true;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "Exact texts case sensitive in any order " + expectedTexts;
  }
}
