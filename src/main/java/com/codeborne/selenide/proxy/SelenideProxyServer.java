package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Config;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Selenide own proxy server to intercept server responses
 *
 * It holds map of request and response filters by name.
 */
public class SelenideProxyServer {
  private final Config config;
  private final InetAddressResolver inetAddressResolver;
  private final Proxy outsideProxy;
  private final BrowserMobProxy proxy;
  private final Map<String, RequestFilter> requestFilters = new HashMap<>();
  private final Map<String, ResponseFilter> responseFilters = new HashMap<>();
  private int port;

  /**
   * Create server
   * Note that server is not started nor activated yet.
   *
   * @param outsideProxy another proxy server used by test author for his own need (can be null)
   */
  public SelenideProxyServer(Config config, Proxy outsideProxy) {
    this(config, outsideProxy, new InetAddressResolver(), new BrowserMobProxyServerUnlimited());
  }

  protected SelenideProxyServer(Config config, Proxy outsideProxy, InetAddressResolver inetAddressResolver, BrowserMobProxy proxy) {
    this.config = config;
    this.outsideProxy = outsideProxy;
    this.inetAddressResolver = inetAddressResolver;
    this.proxy = proxy;
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

    addRequestFilter("authentication", new AuthenticationFilter());
    addRequestFilter("requestSizeWatchdog", new RequestSizeWatchdog());
    addResponseFilter("responseSizeWatchdog", new ResponseSizeWatchdog());
    addResponseFilter("download", new FileDownloadFilter(config));

    proxy.start(config.proxyPort());
    port = proxy.getPort();
  }

  public boolean isStarted() {
    return proxy.isStarted();
  }

  /**
   * Add a custom request filter which allows to track/modify all requests from browser to server
   *
   * @param name unique name of filter
   * @param requestFilter the filter
   */
  public void addRequestFilter(String name, RequestFilter requestFilter) {
    if (isRequestFilterAdded(name)) {
      throw new IllegalArgumentException("Duplicate request filter: " + name);
    }
    proxy.addRequestFilter(requestFilter);
    requestFilters.put(name, requestFilter);
  }

  private boolean isRequestFilterAdded(String name) {
    return requestFilters.containsKey(name);
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
    return isEmpty(config.proxyHost())
      ? ClientUtil.createSeleniumProxy(proxy)
      : ClientUtil.createSeleniumProxy(proxy, inetAddressResolver.getInetAddressByName(config.proxyHost()));
  }

  /**
   * Stop the server
   */
  public void shutdown() {
    if (proxy.isStarted()) {
      try {
        proxy.abort();
      }
      catch (IllegalStateException ignore) {
      }
    }
  }

  /**
   * Method return current instance of browser mob proxy
   *
   * @return browser mob proxy instance
   */
  public BrowserMobProxy getProxy() {
    return proxy;
  }

  @Override
  public String toString() {
    return String.format("Selenide proxy server: %s", port);
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
