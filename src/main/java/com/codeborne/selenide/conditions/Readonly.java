package com.codeborne.selenide.conditions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Readonly extends Attribute {
  public Readonly() {
    super("readonly");
  }

  @Nonnull
  @Override
  public String toString() {
    return "readonly";
  }
}
