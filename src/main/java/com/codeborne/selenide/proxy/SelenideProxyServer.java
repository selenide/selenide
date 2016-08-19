package com.codeborne.selenide.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.ResponseFilter;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Selenide own proxy server to intercept server responses
 * 
 * It holds map of response filters by name.
 */
public class SelenideProxyServer {
  protected final Proxy outsideProxy;
  protected BrowserMobProxy proxy = new BrowserMobProxyServer();
  protected int port;
  protected Map<String, ResponseFilter> filters = new HashMap<>();

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
    proxy.start();
    port = proxy.getPort();

    FileDownloadFilter fileDownloadFilter = new FileDownloadFilter();
    addResponseFilter(fileDownloadFilter);
  }

  private void addResponseFilter(FileDownloadFilter fileDownloadFilter) {
    proxy.addResponseFilter(fileDownloadFilter);
    filters.put("download", fileDownloadFilter);
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
    proxy.stop();
  }

  @Override
  public String toString() {
    return String.format("Selenide proxy server :%s", port);
  }

  /**
   * Get response filter by name
   * 
   * By default, the only one filter "download" is available.
   */
  @SuppressWarnings("unchecked")
  public <T extends ResponseFilter> T filter(String name) {
    return (T) filters.get(name);
  }
}
