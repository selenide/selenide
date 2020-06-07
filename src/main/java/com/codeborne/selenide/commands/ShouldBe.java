package com.codeborne.selenide.commands;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShouldBe extends Should {
  public ShouldBe() {
    super("be ");
  }
}
