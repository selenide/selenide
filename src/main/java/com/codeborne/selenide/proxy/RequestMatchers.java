package com.codeborne.selenide.proxy;

import com.codeborne.selenide.proxy.RequestMatcher.HttpMethod;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.function.Function;
import java.util.regex.Pattern;

import static com.codeborne.selenide.proxy.RequestMatcher.methodMatches;

@Nonnull
@CheckReturnValue
@ParametersAreNonnullByDefault
public class RequestMatchers {
  public static RequestMatcher urlEquals(HttpMethod method, String url) {
    return urlMatcher(method, requestUrl -> requestUrl.equals(url));
  }

  public static RequestMatcher urlStartsWith(HttpMethod method, String url) {
    return urlMatcher(method, requestUrl -> requestUrl.startsWith(url));
  }

  public static RequestMatcher urlEndsWith(HttpMethod method, String url) {
    return urlMatcher(method, requestUrl -> requestUrl.endsWith(url));
  }

  public static RequestMatcher urlContains(HttpMethod method, String url) {
    return urlMatcher(method, requestUrl -> requestUrl.contains(url));
  }

  public static RequestMatcher urlMatches(HttpMethod method, String urlRegex) {
    return urlMatches(method, Pattern.compile(urlRegex));
  }

  public static RequestMatcher urlMatches(HttpMethod method, Pattern urlRegex) {
    return urlMatcher(method, requestUrl -> urlRegex.matcher(requestUrl).matches());
  }

  public static RequestMatcher urlMatcher(HttpMethod method, Function<String, Boolean> urlMatches) {
    return (request, contents, messageInfo) ->
      methodMatches(request, method) &&
        urlMatches.apply(messageInfo.getUrl());
  }
}
