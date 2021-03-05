package com.codeborne.selenide;

public interface Clipboard {

  /**
   * Get text from clipboard
   *
   * @return string content of clipboard
   */
  String getText();

  /**
   * Set text to clipboard
   *
   * @param text value to be set to clipboard
   */
  void setText(String text);

  /**
   * Check that value in clipboard equals to expected
   *
   * @param text expected value for compare
   */
  void shouldBeText(String text);

}
