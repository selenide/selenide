package com.codeborne.selenide.conditions;

import static java.util.Arrays.asList;

public class Interactable extends Or {
  public Interactable() {
    super("interactable", asList(new Visible(), new CssValue("opacity", "0")));
  }

  @Override
  public String toString() {
    return getName();
  }
}
