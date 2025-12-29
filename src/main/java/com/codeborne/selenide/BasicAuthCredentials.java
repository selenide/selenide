package com.codeborne.selenide;

import java.util.Base64;

import static com.codeborne.selenide.ValueMasker.mask;
import static java.nio.charset.StandardCharsets.UTF_8;

public record BasicAuthCredentials(
  String domain,
  String login,
  String password
) implements Credentials {
  /**
   * Security warning:
   * If you are using Selenide proxy, use another constructor (with domain parameter).
   * This constructor is dangerous: without domain specified, Selenide proxy will send your credentials to ALL
   * domains, including 3rd party services that your AUT or browser might call.
   * <p>
   * If proxy is disabled, it's totally ok to use this constructor.
   */
  public BasicAuthCredentials(String login, String password) {
    this("", login, password);
  }

  /**
   * The resulting string is base64 encoded (e.g. "YWxhZGRpbjpvcGVuc2VzYW1l").
   *
   * @return encoded string
   */
  @Override
  public String encode() {
    byte[] credentialsBytes = combine().getBytes(UTF_8);
    return Base64.getEncoder().encodeToString(credentialsBytes);
  }

  private String combine() {
    return String.format("%s:%s", login, password);
  }

  @Override
  public String toString() {
    return String.format("%s:%s:%s", domain, login, mask(password));
  }
}
