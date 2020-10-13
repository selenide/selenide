package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;

import org.openqa.selenium.WebElement;

import java.util.List;

public class ExactTextsCaseSensitiveInAnyOrder extends ExactTexts {

  public ExactTextsCaseSensitiveInAnyOrder(String... exactTexts) {
    super(exactTexts);
  }

  public ExactTextsCaseSensitiveInAnyOrder(List<String> exactTexts) {
    super(exactTexts);
  }

  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.size() != expectedTexts.size()) {
      return false;
    }

    for (String expectedText : expectedTexts) {
      boolean found = false;
      for (WebElement element : elements) {
        if (Html.text.equalsCaseSensitive(element.getText(), expectedText)) {
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
