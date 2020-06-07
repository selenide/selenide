package com.codeborne.selenide.proxy;

import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.filters.RequestFilterAdapter;
import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.filters.ResponseFilterAdapter;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * By default, BrowserUpProxyServer doesn't allow requests/responses bugger than 2 MB.
 * We need this class to enable bigger sizes.
 */
@ParametersAreNonnullByDefault
class BrowserUpProxyServerUnlimited extends BrowserUpProxyServer {
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
