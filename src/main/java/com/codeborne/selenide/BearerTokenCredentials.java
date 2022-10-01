package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BearerTokenCredentials implements Credentials {
  public final String domain;
  public final String token;

  /**
   * @deprecated Use constructor with domain and token
   */
  @Deprecated
  public BearerTokenCredentials(String token) {
    this("", token);
  }

  public BearerTokenCredentials(String domain, String token) {
    this.domain = domain;
    this.token = token;
  }

  @Nonnull
  @Override
  public String domain() {
    return domain;
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
    return String.format("%s:%s", domain, token);
  }
}
