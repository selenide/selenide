package com.codeborne.selenide;

public final class ValueMasker {
  public static String mask(final CharSequence text) {
    return "*".repeat(text.length());
  }
}
