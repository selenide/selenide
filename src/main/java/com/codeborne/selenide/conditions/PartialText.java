package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
    return Html.text.contains(actualText, expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      new GetSelectedOptionText().execute(driver, element) :
      element.getText();
  }
}
