package com.codeborne.selenide.proxy;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestSizeWatchdog implements RequestFilter {
  private static final Logger log = Logger.getLogger(RequestSizeWatchdog.class.getName());

  int threshold = 2 * 1024 * 1024; // 2 MB

  @Override
  public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (contents.getBinaryContents().length > threshold) {
      log.warning("Too large request " + messageInfo.getUrl() + ": " + contents.getBinaryContents().length + " bytes");
      if (log.isLoggable(Level.FINEST)) {
        log.finest("Request content: " + contents.getTextContents());
      }
    }
    return null;
  }
}
