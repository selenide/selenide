package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class RemoteDriverFactory {
  public WebDriver create(Config config, MutableCapabilities capabilities) {
    Object cdpEnabled = capabilities.getCapability("se:cdpEnabled");
    RemoteWebDriver webDriver = new RemoteWebDriver(remoteUrl(config), capabilities, clientConfig(config));
    webDriver.setFileDetector(new LocalFileDetector());
    if (cdpEnabled != null && webDriver.getCapabilities() instanceof MutableCapabilities webdriverCapabilities) {
      webdriverCapabilities.setCapability("se:cdpEnabled", cdpEnabled);
    }

    return new Augmenter().augment(webDriver);
  }

  private URL remoteUrl(Config config) {
    String remoteUrl = requireNonNull(config.remote(), "Remote browser URL is not configured");
    try {
      return new URL(remoteUrl);
    }
    catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remoteUrl, e);
    }
  }

  private ClientConfig clientConfig(Config config) {
    return ClientConfig.defaultConfig()
      .readTimeout(Duration.ofMillis(config.remoteReadTimeout()))
      .connectionTimeout(Duration.ofMillis(config.remoteConnectionTimeout()));
  }
}
