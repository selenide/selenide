package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import org.openqa.selenium.WebElement;

public class Text extends CaseInsensitiveTextCondition {

  private final GetSelectedOptionText getSelectedOptionsTexts;

  public Text(String text) {
    this(text, new GetSelectedOptionText());
  }

  Text(final String text, GetSelectedOptionText getSelectedOptionsTexts) {
    super("text", text);
    this.getSelectedOptionsTexts = getSelectedOptionsTexts;
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts.execute(driver, element) :
      element.getText();
  }
}
