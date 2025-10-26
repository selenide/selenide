package com.codeborne.selenide;

public enum TextCheck {
  /**
   * <p>Match the full text</p>
   * When this is enabled, command {@code $("h1").shouldHave(text("Hello"))}
   * matches element {@code <h1>Hello</h1>}, but does not match {@code <h1>Hello World</h1>}
   * <p>
   * NB! It will be the default behaviour of {@code $.shouldHave(text)} since Selenide 8.0.0
   * </p>
   */
  FULL_TEXT,

  /**
   * <p>Match the partial text</p>
   * When this is enabled, command {@code $("h1").shouldHave(text("Hello"))}
   * matches both elements {@code <h1>Hello</h1>} and {@code <h1>Hello World</h1>}
   * <p>
   * It is the default behaviour of {@code $.shouldHave(text)} before Selenide 8.0.0.
   * </p>
   */
  PARTIAL_TEXT
}
