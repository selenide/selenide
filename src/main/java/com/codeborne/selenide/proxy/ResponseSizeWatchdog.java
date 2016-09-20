package com.codeborne.selenide.proxy;

import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseSizeWatchdog implements ResponseFilter {
  private static final Logger log = Logger.getLogger(ResponseSizeWatchdog.class.getName());

  int threshold = 2 * 1024 * 1024; // 2 MB

  @Override
  public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (contents.getBinaryContents().length > threshold) {
      log.warning("Too large response " + messageInfo.getUrl() + ": " + contents.getBinaryContents().length + " bytes");
      if (log.isLoggable(Level.FINEST)) {
        log.finest("Response content: " + contents.getTextContents());
      }
    }
  }
}
