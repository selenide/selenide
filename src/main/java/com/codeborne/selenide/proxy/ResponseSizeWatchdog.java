package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseSizeWatchdog implements ResponseFilter {
  private static final Logger log = LoggerFactory.getLogger(ResponseSizeWatchdog.class);

  private final int threshold = 4 * 1024 * 1024; // 4 MB

  @Override
  public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (contents.getBinaryContents().length > threshold) {
      log.warn("Too large response {}: {} bytes", messageInfo.getUrl(), contents.getBinaryContents().length);
      log.trace("Response content: {}", contents.getTextContents());
    }
  }
}
