package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.GetOwnText.getOwnText;

public class ExactOwnText extends TextCondition {
  public ExactOwnText(String expectedText) {
    super("exact own text", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.equals(actualText, expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return getOwnText(driver, element);
  }
}
