package com.codeborne.selenide;

import java.util.regex.Pattern;

public class BearerTokenCredentials implements Credentials {
  private static final Pattern REGEX_ANY_CHAR = Pattern.compile(".");
  public final String domain;
  public final String token;

  public BearerTokenCredentials(String domain, String token) {
    this.domain = domain;
    this.token = token;
  }

  @Override
  public String domain() {
    return domain;
  }

  @Override
  public String encode() {
    return token;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", domain, REGEX_ANY_CHAR.matcher(token).replaceAll("*"));
  }
}
