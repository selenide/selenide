package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Credentials {
  @CheckReturnValue
  @Nonnull
  String encode();

  @CheckReturnValue
  @Nonnull
  String domain();
}
