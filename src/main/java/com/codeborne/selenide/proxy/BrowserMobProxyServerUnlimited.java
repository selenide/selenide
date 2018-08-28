package com.codeborne.selenide.proxy;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.RequestFilterAdapter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.filters.ResponseFilterAdapter;

/**
 * By default, BrowserMobProxyServer doesn't allow requests/responses bugger than 2 MB.
 * We need this class to enable bigger sizes.
 */
class BrowserMobProxyServerUnlimited extends BrowserMobProxyServer {
  private static final int maxSize = 64 * 1024 * 1024; // 64 MB

  @Override
  public void addRequestFilter(RequestFilter filter) {
    addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, maxSize));
  }

  @Override
  public void addResponseFilter(ResponseFilter filter) {
    addLastHttpFilterFactory(new ResponseFilterAdapter.FilterSource(filter, maxSize));
  }
}
