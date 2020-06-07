package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public final class Credentials {
  public final String login;
  public final String password;

  public Credentials(@Nonnull String login, @Nonnull String password) {
    this.login = login;
    this.password = password;
  }

  /**
   * The resulting string is base64 encoded (YWxhZGRpbjpvcGVuc2VzYW1l).
   *
   * @return encoded string
   */
  @CheckReturnValue
  @Nonnull
  public String encode() {
    byte[] credentialsBytes = combine().getBytes(UTF_8);
    return Base64.getEncoder().encodeToString(credentialsBytes);
  }

  private String combine() {
    return String.format("%s:%s", login, password);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return combine();
  }
}
