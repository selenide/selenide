package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Texts extends ExactTexts {
  public Texts(String... expectedTexts) {
    super(expectedTexts);
  }

  @Override
  public boolean apply(List<WebElement> elements) {
    if (elements.size() != expectedTexts.length) {
      return false;
    }

    for (int i = 0; i < expectedTexts.length; i++) {
      WebElement element = elements.get(i);
      String expectedText = expectedTexts[i];
      if (!Html.text.contains(element.getText(), expectedText)) {
        return false;
      }
    }
    return true;
  }
}