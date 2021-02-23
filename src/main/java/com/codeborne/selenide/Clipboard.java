package com.codeborne.selenide;

public interface Clipboard {

  /**
   * Get text from clipboard
   * @return
   */
  String getText();

  /**
   * Set value to clipboard
   * @param text value to be set to clipboard
   */

  void setValue(String text);


  /**
   * Check that value in clipboard equals to expected
   * @param text expected value for compare
   */
  void shouldBeText(String text);

}
