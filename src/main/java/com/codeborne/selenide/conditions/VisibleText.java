package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class VisibleText extends CaseInsensitiveTextCondition {
  private static final JavaScript js = new JavaScript("visible-text.js");

  private final GetSelectedOptionText getSelectedOptionsTexts;

  public VisibleText(String expectedText) {
    this(expectedText, new GetSelectedOptionText());
  }

  VisibleText(String expectedText, GetSelectedOptionText getSelectedOptionsTexts) {
    super("visible text", expectedText);
    this.getSelectedOptionsTexts = getSelectedOptionsTexts;
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts.execute(driver, element) :
      requireNonNull(js.execute(driver, element));
  }
}
