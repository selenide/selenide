package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TextsInAnyOrder extends ExactTexts {
  public TextsInAnyOrder(String... expectedTexts) {
    super(expectedTexts);
  }

  public TextsInAnyOrder(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @CheckReturnValue
  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.size() != expectedTexts.size()) {
      return false;
    }

    List<String> elementsTexts = elements.stream().map(WebElement::getText).collect(Collectors.toList());

    for (String expectedText : expectedTexts) {
      boolean found = false;
      for (String elementText : elementsTexts) {
        if (Html.text.contains(elementText, expectedText)) {
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
    return "TextsInAnyOrder " + expectedTexts;
  }
}
