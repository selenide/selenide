package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Base64;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class BasicAuthCredentials implements Credentials {
  private static final Pattern REGEX_ANY_CHAR = Pattern.compile(".");

  public final String domain;
  public final String login;
  public final String password;

  /**
   * Security warning:
   * If you are using Selenide proxy, use another constructor (with domain parameter).
   * This constructor is dangerous: without domain specified, Selenide proxy will send your credentials to ALL
   * domains, including 3rd party services that your AUT or browser might call.
   *
   * If proxy is disabled, it's totally ok to use this constructor.
   */
  public BasicAuthCredentials(String login, String password) {
    this("", login, password);
  }

  public BasicAuthCredentials(String domain, String login, String password) {
    this.domain = domain;
    this.login = login;
    this.password = password;
  }

  @Nonnull
  @Override
  public String domain() {
    return domain;
  }

  /**
   * The resulting string is base64 encoded (YWxhZGRpbjpvcGVuc2VzYW1l).
   *
   * @return encoded string
   */
  @Override
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
    return String.format("%s:%s:%s", domain, login, REGEX_ANY_CHAR.matcher(password).replaceAll("*"));
  }
}
