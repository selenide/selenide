package com.codeborne.selenide.commands;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShouldNotBe extends ShouldNot {
  public ShouldNotBe() {
    super("be ");
  }
}
