package com.codeborne.selenide.proxy;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.filters.ResponseFilter;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.net.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.browserup.bup.client.ClientUtil.getConnectableAddress;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Selenide own proxy server to intercept server responses
 * <p>
 * It holds map of request and response filters by name.
 */
@ParametersAreNonnullByDefault
public class SelenideProxyServer {
  private static final Logger log = LoggerFactory.getLogger(SelenideProxyServer.class);
  private static final Pattern REGEX_HOST_NAME = Pattern.compile("(.*):.*");
  private static final Pattern REGEX_PORT = Pattern.compile(".*:(.*)");

  private final Config config;
  @Nullable
  private final Proxy outsideProxy;
  @Nullable
  private Proxy seleniumProxy;
  private final BrowserUpProxy proxy;
  private final Map<String, RequestFilter> requestFilters = new HashMap<>();
  private final Map<String, ResponseFilter> responseFilters = new HashMap<>();
  private int port;

  /**
   * Create server
   * Note that server is not started nor activated yet.
   *
   * @param outsideProxy another proxy server used by test author for his own need (can be null)
   */
  public SelenideProxyServer(Config config, @Nullable Proxy outsideProxy) {
    this(config, outsideProxy, new BrowserUpProxyServerUnlimited());
  }

  protected SelenideProxyServer(Config config, @Nullable Proxy outsideProxy,
                                BrowserUpProxy proxy) {
    this.config = config;
    this.outsideProxy = outsideProxy;
    this.proxy = proxy;
  }

  /**
   * Returns a "selenium" proxy that can be used by webdriver.
   * Available after SelenideProxyServer.start() and SelenideProxyServer.createSeleniumProxy() call.
   */
  @Nullable
  public synchronized Proxy getSeleniumProxy() {
    if (seleniumProxy == null) {
      seleniumProxy = createSeleniumProxy();
    }
    return seleniumProxy;
  }

  /**
   * Start the server
   * <p>
   * It automatically adds one response filter "download" that can intercept downloaded files.
   */
  public void start() {
    proxy.setTrustAllServers(true);
    if (outsideProxy != null) {
      proxy.setChainedProxy(getProxyAddress(outsideProxy));
      String noProxy = outsideProxy.getNoProxy();
      if (noProxy != null) {
        List<String> noProxyHosts = Arrays.asList(noProxy.split(","));
        proxy.setChainedProxyNonProxyHosts(noProxyHosts);
      }
    }
    FileDownloadFilter downloadFilter = new FileDownloadFilter(config);

    addRequestFilter("mockResponse", new MockResponseFilter());
    addRequestFilter("authentication", new AuthenticationFilter());
    addRequestFilter("requestSizeWatchdog", new RequestSizeWatchdog());
    addResponseFilter("responseSizeWatchdog", new ResponseSizeWatchdog());
    addRequestFilter("download", downloadFilter);
    addResponseFilter("download", downloadFilter);

    proxy.start(config.proxyPort());
    port = proxy.getPort();
  }

  @CheckReturnValue
  public boolean isStarted() {
    return proxy.isStarted();
  }

  /**
   * Add a custom request filter which allows to track/modify all requests from browser to server
   *
   * @param name          unique name of filter
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
   * @param name           unique name of filter
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
    String host = REGEX_HOST_NAME.matcher(httpProxy).replaceFirst("$1");
    String port = REGEX_PORT.matcher(httpProxy).replaceFirst("$1");
    return new InetSocketAddress(host, parseInt(port));
  }

  /**
   * Converts this proxy to a "selenium" proxy that can be used by webdriver
   */
  @CheckReturnValue
  @Nonnull
  private Proxy createSeleniumProxy() {
    return isEmpty(config.proxyHost())
      ? ClientUtil.createSeleniumProxy(this.proxy, guessHostName())
      : ClientUtil.createSeleniumProxy(this.proxy, config.proxyHost());
  }

  private String guessHostName() {
    String browserupHostName = getConnectableAddress().getHostAddress();
    String seleniumHostName = new NetworkUtils().getNonLoopbackAddressOfThisMachine();
    if (Objects.equals(browserupHostName, seleniumHostName)) {
      log.info("Using proxy host: '{}'", seleniumHostName);
    }
    else {
      log.info("Using proxy host resolved by Selenium: '{}' (fyi BrowserUpProxy resolved : '{}')", seleniumHostName, browserupHostName);
    }
    return seleniumHostName;
  }

  /**
   * Stop the server
   */
  public void shutdown() {
    if (proxy.isStarted()) {
      try {
        proxy.stop();
      }
      catch (IllegalStateException ignore) {
      }
    }
  }

  /**
   * Method return current instance of browser up proxy
   *
   * @return browser up proxy instance
   */
  @CheckReturnValue
  @Nonnull
  public BrowserUpProxy getProxy() {
    return proxy;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return String.format("Selenide proxy server: %s", port);
  }

  /**
   * Get request filter by name
   */
  @SuppressWarnings("unchecked")
  @CheckReturnValue
  @Nullable
  public <T extends RequestFilter> T requestFilter(String name) {
    return (T) requestFilters.get(name);
  }

  /**
   * Get response filter by name
   * <p>
   * By default, the only one filter "download" is available.
   */
  @SuppressWarnings("unchecked")
  @CheckReturnValue
  @Nullable
  public <T extends ResponseFilter> T responseFilter(String name) {
    return (T) responseFilters.get(name);
  }

  public MockResponseFilter responseMocker() {
    return requestFilter("mockResponse");
  }
}
