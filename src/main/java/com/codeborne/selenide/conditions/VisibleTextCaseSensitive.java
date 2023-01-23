package com.codeborne.selenide.conditions;

public class VisibleTextCaseSensitive extends VisibleText {

  public VisibleTextCaseSensitive(String expectedText) {
    super("Case sensitive visible text", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return new ExactTextCaseSensitive(expectedText).match(actualText, expectedText);
  }
}
