package com.codeborne.selenide.conditions.clipboard;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Content implements ObjectCondition<Clipboard> {
  private final String expectedContent;

  public Content(String expectedContent) {
    this.expectedContent = expectedContent;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return String.format("should have content '%s'", expectedContent);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return String.format("should not have content '%s'", expectedContent);
  }

  @CheckReturnValue
  @Override
  public CheckResult check(Clipboard clipboard) {
    String clipboardText = clipboard.getText();
    return result(clipboard, clipboardText.equals(expectedContent), clipboardText);
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return expectedContent;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(Clipboard clipboard) {
    return "clipboard";
  }
}
