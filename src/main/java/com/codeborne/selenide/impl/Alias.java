package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class Alias {
  private final String text;

  public Alias(String text) {
    if (text.isEmpty()) throw new IllegalArgumentException("Empty alias not allowed");
    this.text = text;
  }

  @CheckReturnValue
  public String getOrElse(Supplier<String> defaultValue) {
    return text;
  }

  /**
   * As a rule, you don't need to use this method directly.
   * @return text value of this alias (or empty text if alias is not defined)
   * @since 5.20.0
   */
  @Nullable
  @CheckReturnValue
  public String getText() {
    return text;
  }

  public static final Alias NONE = new NoneAlias();

  private static final class NoneAlias extends Alias {
    NoneAlias() {
      super("-");
    }

    @Override
    @CheckReturnValue
    public String getOrElse(Supplier<String> defaultValue) {
      return defaultValue.get();
    }

    @Override
    @Nullable
    @CheckReturnValue
    public String getText() {
      return null;
    }
  }
}
