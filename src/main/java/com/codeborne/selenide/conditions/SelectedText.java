package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelectedText extends CaseSensitiveTextCondition {
  private final JavaScript js = new JavaScript("get-selected-text.js");

  public SelectedText(String expectedText) {
    super("selected text", expectedText);
  }

  @Nullable
  @CheckReturnValue
  @Override
  protected String getText(Driver driver, WebElement element) {
    return js.execute(driver, element);
  }
}
