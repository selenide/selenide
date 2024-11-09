package com.codeborne.selenide;

/**
 * Authentication schemes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#Authentication_schemes">Web HTTP reference</a>
 */
public enum AuthenticationType {
  BASIC("Basic"),
  BEARER("Bearer"),
  DIGEST("Digest"),
  HOBA("HOBA"),
  MUTUAL("Mutual"),
  AWS4_HMAC_SHA256("AWS4-HMAC-SHA256");

  private final String value;

  AuthenticationType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
