package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class SelectedText extends CaseSensitiveTextCondition {
  private final JavaScript js = new JavaScript("get-selected-text.js");

  public SelectedText(String expectedText) {
    super("selected text", expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return requireNonNull(js.execute(driver, element));
  }
}
