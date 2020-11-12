package com.codeborne.selenide.collections;

import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class Texts extends ExactTexts {
  public Texts(String... expectedTexts) {
    super(expectedTexts);
  }

  public Texts(List<String> expectedTexts) {
    super(expectedTexts);
  }

  @CheckReturnValue
  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.size() != expectedTexts.size()) {
      return false;
    }

    for (int i = 0; i < expectedTexts.size(); i++) {
      WebElement element = elements.get(i);
      String expectedText = expectedTexts.get(i);
      if (!Html.text.contains(element.getText(), expectedText)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "texts " + expectedTexts;
  }
}
