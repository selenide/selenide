package com.codeborne.selenide.impl;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class Alias {
  private final String text;

  public Alias(String text) {
    if (text.isEmpty()) throw new IllegalArgumentException("Empty alias not allowed");
    this.text = text;
  }

  public String getOrElse(Supplier<String> defaultValue) {
    return text;
  }

  public String get(Supplier<String> defaultValue) {
    return text + " {" + defaultValue.get() + '}';
  }

  public String appendable() {
    return getText() == null ? "" : " \"" + getText() + "\"";
  }

  /**
   * As a rule, you don't need to use this method directly.
   * @return text value of this alias (or empty text if alias is not defined)
   */
  @Nullable
  public String getText() {
    return text;
  }

  public static final Alias NONE = new NoneAlias();

  private static final class NoneAlias extends Alias {
    NoneAlias() {
      super("-");
    }

    @Override
    public String getOrElse(Supplier<String> defaultValue) {
      return defaultValue.get();
    }

    @Override
    public String get(Supplier<String> defaultValue) {
      return defaultValue.get();
    }

    @Override
    @Nullable
    public String getText() {
      return null;
    }
  }
}
