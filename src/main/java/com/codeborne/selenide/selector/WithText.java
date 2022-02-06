package com.codeborne.selenide.selector;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WithText extends WithTagAndText {
  public WithText(String elementText) {
    super("*", elementText);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "with text: " + elementText;
  }
}
