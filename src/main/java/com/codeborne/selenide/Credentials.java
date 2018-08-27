package com.codeborne.selenide;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Credentials {
  public final String login;
  public final String password;

  public Credentials(String login, String password) {
    this.login = login;
    this.password = password;
  }

  /**
   * The resulting string is base64 encoded (YWxhZGRpbjpvcGVuc2VzYW1l).
   *
   * @return encoded string
   */
  public String encode() {
    byte[] credentialsBytes = combine().getBytes(UTF_8);
    return Base64.getEncoder().encodeToString(credentialsBytes);
  }

  private String combine() {
    return String.format("%s:%s", login, password);
  }

  @Override
  public String toString() {
    return combine();
  }
}
