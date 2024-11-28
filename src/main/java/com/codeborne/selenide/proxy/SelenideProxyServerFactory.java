package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Proxy;

/**
 * <p>
 * Interface for creating custom SelenideProxyServer in your tests
 * </p>
 */
@FunctionalInterface
public interface SelenideProxyServerFactory {

  /**
   * Creates a {@link SelenideProxyServer}
   * Allows user to change settings of BrowserUpProxy before the proxy is started.
   * Allows user to change settings of Selenium proxy.
   * <b>Must call {@link SelenideProxyServer#start()} to start proxy server.</b>
   * <b>Must call {@link SelenideProxyServer#createSeleniumProxy()} to create Selenium proxy server instance.</b>
   * For implementation example see {@link DefaultSelenideProxyServerFactory}
   *
   * @param config - selenide config
   * @param userProvidedProxy - additional proxy provided from user
   * @return new {@link SelenideProxyServer} instance
   */
  SelenideProxyServer create(Config config, @Nullable Proxy userProvidedProxy);
}
