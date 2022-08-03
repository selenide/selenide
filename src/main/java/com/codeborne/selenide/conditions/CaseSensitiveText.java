package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CaseSensitiveText extends CaseSensitiveTextCondition {

  public CaseSensitiveText(String expectedText) {
    super("text case sensitive", expectedText);
  }

  @Nullable
  @CheckReturnValue
  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts(element) :
      element.getText();
  }
}
