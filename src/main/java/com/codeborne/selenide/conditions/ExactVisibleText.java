package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.GetVisibleText.getVisibleText;

public class ExactVisibleText extends TextCondition {
  private final GetSelectedOptionText getSelectedOptionText;

  public ExactVisibleText(String expectedText) {
    this(expectedText, new GetSelectedOptionText());
  }

  ExactVisibleText(String expectedText, GetSelectedOptionText getSelectedOptionText) {
    super("exact visible text", expectedText);
    this.getSelectedOptionText = getSelectedOptionText;
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.equals(actualText, expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return getVisibleText(driver, element, getSelectedOptionText);
  }
}
