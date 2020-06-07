package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DummyRandomizer extends Randomizer {
  private final String text;

  public DummyRandomizer(String text) {
    this.text = text;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String text() {
    return text;
  }
}
