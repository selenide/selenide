package com.codeborne.selenide;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Credentials {
  private String login;
  private String password;

  public Credentials(final String login, final String password) {
    this.login = login;
    this.password = password;
  }

  /**
   * The resulting string is base64 encoded (YWxhZGRpbjpvcGVuc2VzYW1l).
   *
   * @return encoded string
   */
  public String encode() {
    final byte[] credentialsBytes = combine().getBytes(UTF_8);
    return Base64.getEncoder().encodeToString(credentialsBytes);
  }

  /**
   * Combine credentials with a colon (login:password).
   *
   * @return combined credentials
   */
  private String combine() {
    return String.format("%s:%s", login, password);
  }
}
