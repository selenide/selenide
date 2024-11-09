package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Proxy;

public class DefaultSelenideProxyServerFactory implements SelenideProxyServerFactory {
  @Override
  public SelenideProxyServer create(Config config, @Nullable Proxy userProvidedProxy) {
    SelenideProxyServer selenideProxyServer = new SelenideProxyServer(config, userProvidedProxy);
    selenideProxyServer.start();
    return selenideProxyServer;
  }
}
