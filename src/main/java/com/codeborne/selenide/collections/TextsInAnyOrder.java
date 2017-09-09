package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TextsInAnyOrder extends ExactTexts {
  public TextsInAnyOrder(String... expectedTexts) {
    super(expectedTexts);
  }

  public TextsInAnyOrder(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @Override
  public boolean apply(List<WebElement> elements) {
    if (elements.size() != expectedTexts.size()) {
      return false;
    }

    for (int i = 0; i < expectedTexts.size(); i++) {
      String expectedText = expectedTexts.get(i);
      boolean bFound = false;
      for (WebElement element: elements) {
        if (Html.text.contains(element.getText(), expectedText)) {
          bFound = true;
        }
      }
      if (bFound == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "TextsInAnyOrder " + expectedTexts;
  }
}
