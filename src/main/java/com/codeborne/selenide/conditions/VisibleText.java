package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;

public class VisibleText extends TextCondition {

  private final JavaScript jsSource = new JavaScript("visibleText.js");

  protected VisibleText(String name, String expectedText) {
    super(name, expectedText);
  }

  public VisibleText(String expectedText) {
    super("Case-insensitive visible text", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return new ExactText(expectedText).match(actualText, expectedText);
  }

  @Nullable
  @Override
  protected String getText(Driver driver, WebElement element) {
    return jsSource.execute(driver, element);
  }
}
