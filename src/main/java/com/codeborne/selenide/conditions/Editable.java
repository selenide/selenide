package com.codeborne.selenide.conditions;

import static java.util.Arrays.asList;

public class Editable extends And {
  public Editable() {
    super("editable", asList(new Interactable(), new Enabled(), new Readonly().negate()));
  }

  @Override
  public String toString() {
    return getName();
  }
}
