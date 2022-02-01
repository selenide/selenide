package com.codeborne.selenide.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.time.Duration;

/**
 * A temporary workaround to override default timeouts of NettyClient used in Selenium.
 *
 * Its default timeouts are too long:
 * 1) connectTimeout = 10 seconds  (Selenide sets to 10 seconds)
 * 2) readTimeout = 3 minutes      (Selenide sets to 90 seconds)
 *
 * @since 5.22.0
 */
@ParametersAreNonnullByDefault
class HttpClientTimeouts {
  private static final Logger logger = LoggerFactory.getLogger(HttpClientTimeouts.class);
  public static Duration defaultReadTimeout = Duration.ofSeconds(90);

  public void setup(WebDriver webDriver) {
    setup(webDriver, defaultReadTimeout);
  }

  public void setup(WebDriver webDriver, Duration readTimeout) {
    if (webDriver instanceof RemoteWebDriver) {
      try {
        setupTimeouts((RemoteWebDriver) webDriver, readTimeout);
      }
      catch (Exception e) {
        throw new IllegalStateException("Failed to setup Selenium HttpClient timeouts", e);
      }
    }
  }

  private void setupTimeouts(RemoteWebDriver webDriver, Duration readTimeout) throws Exception {
    CommandExecutor executor = webDriver.getCommandExecutor();
    if (executor instanceof HttpCommandExecutor) {
      setupTimeouts((HttpCommandExecutor) executor, readTimeout);
    }
  }

  private void setupTimeouts(HttpCommandExecutor executor, Duration readTimeout) throws Exception {
    Field clientField = HttpCommandExecutor.class.getDeclaredField("client");
    clientField.setAccessible(true);
    HttpClient client = (HttpClient) clientField.get(executor);
    if (client instanceof NettyClient) {
      setupTimeouts((NettyClient) client, readTimeout);
    }
  }

  private void setupTimeouts(NettyClient client, Duration readTimeout) throws Exception {
    Field configField = NettyClient.class.getDeclaredField("config");
    configField.setAccessible(true);
    Object config = configField.get(client);
    if (config instanceof ClientConfig) {
      setupTimeouts((ClientConfig) config, readTimeout);
    }
  }
  private void setupTimeouts(ClientConfig config, Duration readTimeout) {
    Duration previousConnectTimeout = config.connectionTimeout();
    Duration previousReadTimeout = config.readTimeout();
    config.readTimeout(readTimeout);
    logger.info("Changed connectTimeout from {} to {}", previousConnectTimeout, config.connectionTimeout());
    logger.info("Changed readTimeout from {} to {}", previousReadTimeout, config.readTimeout());
  }
}
