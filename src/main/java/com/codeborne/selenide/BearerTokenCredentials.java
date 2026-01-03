package com.codeborne.selenide;

import static com.codeborne.selenide.ValueMasker.mask;

public record BearerTokenCredentials(
  String domain,
  String token
) implements Credentials {
  @Override
  public String encode() {
    return token;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", domain, mask(token));
  }
}
