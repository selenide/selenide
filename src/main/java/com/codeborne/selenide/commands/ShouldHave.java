package com.codeborne.selenide.commands;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShouldHave extends Should {
  public ShouldHave() {
    super("have ");
  }
}
