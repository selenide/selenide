package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelectedText extends TextCondition {
  private final JavaScript js = new JavaScript("get-selected-text.js");

  public SelectedText(String expectedText) {
    super("selected text", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.containsCaseSensitive(actualText, expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return js.execute(driver, element);
  }
}
