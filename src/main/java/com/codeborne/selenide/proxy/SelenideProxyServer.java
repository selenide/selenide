package com.codeborne.selenide.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.RequestFilterAdapter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.filters.ResponseFilterAdapter;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Selenide own proxy server to intercept server responses
 *
 * It holds map of request and response filters by name.
 */
public class SelenideProxyServer {
  protected final Proxy outsideProxy;
  protected BrowserMobProxy proxy = new BrowserMobProxyServer() {
    int maxSize = 64 * 1024 * 1024; // 64 MB
    @Override
    public void addRequestFilter(RequestFilter filter) {
      addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, maxSize));
    }

    @Override public void addResponseFilter(ResponseFilter filter) {
      addLastHttpFilterFactory(new ResponseFilterAdapter.FilterSource(filter, maxSize));
    }
  };

  protected int port;
  protected Map<String, RequestFilter> requestFilters = new HashMap<>();
  protected Map<String, ResponseFilter> responseFilters = new HashMap<>();

  /**
   * Create server
   * Note that server is not started nor activated yet.
   *
   * @param outsideProxy another proxy server used by test author for his own need (can be null)
   */
  public SelenideProxyServer(Proxy outsideProxy) {
    this.outsideProxy = outsideProxy;
  }

  /**
   * Start the server
   *
   * It automatically adds one response filter "download" that can intercept downloaded files.
   */
  public void start() {
    proxy.setTrustAllServers(true);
    if (outsideProxy != null) {
      proxy.setChainedProxy(getProxyAddress(outsideProxy));
    }

    addRequestFilter("requestSizeWatchdog", new RequestSizeWatchdog());
    addResponseFilter("responseSizeWatchdog", new ResponseSizeWatchdog());
    addResponseFilter("download", new FileDownloadFilter());

    proxy.start();
    port = proxy.getPort();
  }


  /**
   * Add a custom request filter which allows to track/modify all requests from browser to server
   *
   * @param name unique name of filter
   * @param requestFilter the filter
   */
  public void addRequestFilter(String name, RequestFilter requestFilter) {
    if (requestFilters.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate request filter: " + name);
    }

    proxy.addRequestFilter(requestFilter);
    requestFilters.put(name, requestFilter);
  }

  /**
   * Add a custom response filter which allows to track/modify all server responses to browser
   *
   * @param name unique name of filter
   * @param responseFilter the filter
   */
  public void addResponseFilter(String name, ResponseFilter responseFilter) {
    if (responseFilters.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate response filter: " + name);
    }
    proxy.addResponseFilter(responseFilter);
    responseFilters.put(name, responseFilter);
  }

  static InetSocketAddress getProxyAddress(Proxy proxy) {
    String httpProxy = proxy.getHttpProxy();
    String host = httpProxy.replaceFirst("(.*):.*", "$1");
    String port = httpProxy.replaceFirst(".*:(.*)", "$1");
    return new InetSocketAddress(host, parseInt(port));
  }

  /**
   * Converts this proxy to a "selenium" proxy that can be used by webdriver
   */
  public Proxy createSeleniumProxy() {
    return ClientUtil.createSeleniumProxy(proxy);
  }

  /**
   * Stop the server
   */
  public void shutdown() {
    proxy.abort();
  }

  @Override
  public String toString() {
    return String.format("Selenide proxy server :%s", port);
  }

  /**
   * Get request filter by name
   */
  @SuppressWarnings("unchecked")
  public <T extends RequestFilter> T requestFilter(String name) {
    return (T) requestFilters.get(name);
  }

  /**
   * Get response filter by name
   *
   * By default, the only one filter "download" is available.
   */
  @SuppressWarnings("unchecked")
  public <T extends ResponseFilter> T responseFilter(String name) {
    return (T) responseFilters.get(name);
  }
}
