package com.codeborne.selenide;

public interface Clipboard extends Conditional<Clipboard> {

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
}
