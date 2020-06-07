package com.codeborne.selenide.commands;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShouldNotHave extends ShouldNot {
  public ShouldNotHave() {
    super("have ");
  }
}
