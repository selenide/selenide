package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class BearerTokenCredentials implements Credentials {
  private static final Pattern REGEX_ANY_CHAR = Pattern.compile(".");
  public final String domain;
  public final String token;

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
    return String.format("%s:%s", domain, REGEX_ANY_CHAR.matcher(token).replaceAll("*"));
  }
}
