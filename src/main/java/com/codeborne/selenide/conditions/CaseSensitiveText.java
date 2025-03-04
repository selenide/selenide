package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import org.openqa.selenium.WebElement;

public class CaseSensitiveText extends CaseSensitiveTextCondition {
  private final GetSelectedOptionText getSelectedOptionText;

  public CaseSensitiveText(String expectedText) {
    this(expectedText, new GetSelectedOptionText());
  }

  CaseSensitiveText(String expectedText, GetSelectedOptionText getSelectedOptionText) {
    super("text case sensitive", expectedText);
    this.getSelectedOptionText = getSelectedOptionText;
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionText.execute(driver, element) :
      element.getText();
  }
}
