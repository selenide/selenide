package com.codeborne.selenide.conditions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Arrays.asList;

@ParametersAreNonnullByDefault
public class Interactable extends Or {
  public Interactable() {
    super("interactable", asList(new Visible(), new CssValue("opacity", "0")));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return getName();
  }
}
