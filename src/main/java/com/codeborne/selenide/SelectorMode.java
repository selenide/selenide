package com.codeborne.selenide;

public enum SelectorMode {
  /**
   * Default Selenium behavior
   */
  CSS,

  /**
   * Use Sizzle for CSS selectors.
   * It allows powerful CSS3 selectors - ":input", ":not", ":nth", ":first", ":last", ":contains('text')"
   *
   * For other selectors (XPath, ID etc.) uses default Selenium mechanism.
   */
  Sizzle
}
