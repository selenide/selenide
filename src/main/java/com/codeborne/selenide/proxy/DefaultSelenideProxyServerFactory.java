package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultSelenideProxyServerFactory implements SelenideProxyServerFactory {

  @Nonnull
  @Override
  public ProxyPair createProxies(Config config, @Nullable Proxy userProvidedProxy) {
    SelenideProxyServer selenideProxyServer = new SelenideProxyServer(config, userProvidedProxy);
    selenideProxyServer.start();
    Proxy seleniumProxy = selenideProxyServer.createSeleniumProxy();
    return new ProxyPair(selenideProxyServer, seleniumProxy);
  }
}
