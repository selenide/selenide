package com.codeborne.selenide.conditions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Arrays.asList;

@ParametersAreNonnullByDefault
public class Editable extends And {
  public Editable() {
    super("editable", asList(new Interactable(), new Enabled(), new Readonly().negate()));
  }

  @Nonnull
  @Override
  public String toString() {
    return getName();
  }
}
