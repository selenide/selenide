package com.codeborne.selenide.webdriver;

import com.google.auto.service.AutoService;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpClientName;
import org.openqa.selenium.remote.http.netty.NettyClient;

import static com.codeborne.selenide.webdriver.HttpClientTimeouts.defaultReadTimeout;

@AutoService(HttpClient.Factory.class)
@HttpClientName("selenide-netty-client-factory")
public class SelenideNettyClientFactory extends NettyClient.Factory {

  @Override
  public HttpClient createClient(ClientConfig config) {
    ClientConfig configWithShorterTimeout = config.readTimeout(defaultReadTimeout);
    return super.createClient(configWithShorterTimeout);
  }
}
