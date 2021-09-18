package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @since 5.20.0
 */
@ParametersAreNonnullByDefault
public interface Clipboard extends Conditional<Clipboard> {

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
