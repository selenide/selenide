package com.codeborne.selenide.proxy;

import io.netty.handler.codec.http.HttpResponse;
import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ResponseSizeWatchdog implements ResponseFilter {
  private static final Logger log = LoggerFactory.getLogger(ResponseSizeWatchdog.class);

  int threshold = 2 * 1024 * 1024; // 2 MB

  @Override
  public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (contents.getBinaryContents().length > threshold) {
      log.warn("Too large response {}: {} bytes", messageInfo.getUrl(), contents.getBinaryContents().length);
      log.trace("Response content: {}", contents.getTextContents());
    }
  }
}
