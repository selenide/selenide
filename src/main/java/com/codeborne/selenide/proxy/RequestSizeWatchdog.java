package com.codeborne.selenide.proxy;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestSizeWatchdog implements RequestFilter {
  private static final Logger log = LoggerFactory.getLogger(RequestSizeWatchdog.class);

  int threshold = 2 * 1024 * 1024; // 2 MB

  @Override
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (contents.getBinaryContents().length > threshold) {
      log.warn("Too large request {}: {} bytes", messageInfo.getUrl(), contents.getBinaryContents().length);
      log.trace("Request content: {}", contents.getTextContents());
    }
    return null;
  }
}
