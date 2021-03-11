package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface Clipboard {

  /**
   * Get text from clipboard
   *
   * @return string content of clipboard
   */
  @CheckReturnValue
  @Nonnull
  String getText();

  /**
   * Set text to clipboard
   *
   * @param text value to be set to clipboard
   */
  void setText(String text);

}
