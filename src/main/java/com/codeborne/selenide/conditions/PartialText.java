package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@ParametersAreNonnullByDefault
public class PartialText extends TextCondition {

  public PartialText(String text) {
    super("partial text", text);

    if (isEmpty(text)) {
      throw new IllegalArgumentException("Argument must not be null or empty string. " +
        "Use $.shouldBe(empty) or $.shouldHave(exactText(\"\").");
    }
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.contains(actualText, expectedText.toLowerCase(ROOT));
  }

  @Nullable
  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts(element) :
      element.getText();
  }
}
