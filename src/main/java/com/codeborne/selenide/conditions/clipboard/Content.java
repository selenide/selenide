package com.codeborne.selenide.conditions.clipboard;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ObjectCondition;

public class Content implements ObjectCondition<Clipboard> {
  private final String expectedContent;

  public Content(String expectedContent) {
    this.expectedContent = expectedContent;
  }

  @Override
  public String description() {
    return String.format("should have content '%s'", expectedContent);
  }

  @Override
  public String negativeDescription() {
    return String.format("should not have content '%s'", expectedContent);
  }

  @Override
  public CheckResult check(Clipboard clipboard) {
    String clipboardText = clipboard.getText();
    return result(clipboard, clipboardText.equals(expectedContent), clipboardText);
  }

  @Override
  public String expectedValue() {
    return expectedContent;
  }

  @Override
  public String describe(Clipboard clipboard) {
    return "clipboard";
  }
}
