package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelectedText extends TextCondition {
  public SelectedText(String expectedText) {
    super("selected text", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.containsCaseSensitive(actualText, expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return driver.executeJavaScript(
      "return arguments[0].value.substring(arguments[0].selectionStart, arguments[0].selectionEnd);", element);
  }
}
