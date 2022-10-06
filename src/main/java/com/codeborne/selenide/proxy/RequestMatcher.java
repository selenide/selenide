package com.codeborne.selenide.proxy;

import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import io.netty.handler.codec.http.HttpRequest;

public interface RequestMatcher {
  enum HttpMethod {
    DELETE,
    GET,
    POST,
    PUT,
    OPTIONS,
    PATCH;
  }

  boolean match(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo);

  static boolean methodMatches(HttpRequest request, HttpMethod method) {
    return request.method().name().equalsIgnoreCase(method.name()) ||
      request.method().name().equalsIgnoreCase(HttpMethod.OPTIONS.name());
  }
}
