package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class RemoteDriverFactory {
  public WebDriver create(Config config, MutableCapabilities capabilities) {
    try {
      CommandExecutor commandExecutor = createExecutor(config);
      RemoteWebDriver webDriver = new RemoteWebDriver(commandExecutor, capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }
    catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + config.remote(), e);
    }
  }

  private CommandExecutor createExecutor(Config config) throws MalformedURLException {
    String remoteUrl = requireNonNull(config.remote(), "Remote browser URL is not configured");
    ClientConfig clientConfig = ClientConfig.defaultConfig()
      .baseUrl(new URL(remoteUrl))
      .readTimeout(Duration.ofMillis(config.remoteReadTimeout()))
      .connectionTimeout(Duration.ofMillis(config.remoteConnectionTimeout()));

    return new HttpCommandExecutor(clientConfig);
  }
}
