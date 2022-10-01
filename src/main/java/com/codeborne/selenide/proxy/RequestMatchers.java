package com.codeborne.selenide.proxy;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Nonnull
@CheckReturnValue
@ParametersAreNonnullByDefault
public class RequestMatchers {
  public static RequestMatcher urlEquals(String url) {
    return (request, contents, messageInfo) -> messageInfo.getUrl().equals(url);
  }

  public static RequestMatcher urlStartsWith(String url) {
    return (request, contents, messageInfo) -> messageInfo.getUrl().endsWith(url);
  }

  public static RequestMatcher urlEndsWith(String url) {
    return (request, contents, messageInfo) -> messageInfo.getUrl().endsWith(url);
  }
}
