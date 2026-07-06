package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.GetVisibleText.getVisibleText;

public class VisibleText extends CaseInsensitiveTextCondition {

  private final GetSelectedOptionText getSelectedOptionText;

  public VisibleText(String expectedText) {
    this(expectedText, new GetSelectedOptionText());
  }

  VisibleText(String expectedText, GetSelectedOptionText getSelectedOptionText) {
    super("visible text", expectedText);
    this.getSelectedOptionText = getSelectedOptionText;
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return getVisibleText(driver, element, getSelectedOptionText);
  }
}
