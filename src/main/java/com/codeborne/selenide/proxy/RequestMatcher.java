package com.codeborne.selenide.proxy;

import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import io.netty.handler.codec.http.HttpRequest;

public interface RequestMatcher {
  boolean match(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo);
}
