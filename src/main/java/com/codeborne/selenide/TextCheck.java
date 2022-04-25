package com.codeborne.selenide;

/**
 * @since 6.5.0
 */
public enum TextCheck {
  /**
   * <p>Match the full text.</p>
   * <p>
   * It's a new default behaviour of {@code $.shouldHave(text)} since Selenide 6.5.0
   * </p>
   */
  FULL_TEXT,

  /**
   * <p>Match the partial text</p>
   * <p>
   * It was the default behaviour of {@code $.shouldHave(text)} until Selenide 6.4.0.
   * </p>
   * <p>Left here for compatibility:
   * use it if you have you too many failing tests after upgrading to Selenide 6.5.0.
   * </p>
   */
  PARTIAL_TEXT
}
