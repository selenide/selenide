package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BearerTokenCredentials implements Credentials {
  public final String token;

  public BearerTokenCredentials(String token) {
    this.token = token;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String encode() {
    return token;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return encode();
  }
}
