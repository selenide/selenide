package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.UUID;

public class Randomizer {
  @CheckReturnValue
  @Nonnull
  public String text() {
    return UUID.randomUUID().toString();
  }
}
