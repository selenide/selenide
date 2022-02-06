package com.codeborne.selenide.selector;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ByText extends ByTagAndText {
  public ByText(String elementText) {
    super("*", elementText);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "by text: " + elementText;
  }
}
