package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * <p>
 * Interface for using custom Selenide Proxy Factory in your tests
 * </p>
 */
@ParametersAreNonnullByDefault
public interface SelenideProxyServerFactory {

  /**
   * Create a pair of {@link SelenideProxyServer} and {@link Proxy}.
   * Allows user to change settings of BrowserUpProxy before the proxy is started.
   * Allows user to change settings of Selenium proxy.
   * <b>Must call SelenideProxyServer.start() to start proxy server.<b>
   * For implementation example see {@link DefaultSelenideProxyServerFactory}
   *
   * @param config - selenide config
   * @param userProvidedProxy - additional proxy provided from user
   * @return new {@link ProxyPair} instance
   */
  @Nonnull
  @CheckReturnValue
  ProxyPair createProxies(Config config, @Nullable Proxy userProvidedProxy);

  @ParametersAreNonnullByDefault
  class ProxyPair {
    public final SelenideProxyServer selenideProxyServer;
    public final Proxy seleniumProxy;

    public ProxyPair(SelenideProxyServer selenideProxyServer, Proxy seleniumProxy) {
      this.selenideProxyServer = selenideProxyServer;
      this.seleniumProxy = seleniumProxy;
    }
  }
}
