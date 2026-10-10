package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.quote;

public class RemoteDriverFactory {
  public WebDriver create(Config config, MutableCapabilities capabilities) {
    Object cdpEnabled = capabilities.getCapability("se:cdpEnabled");
    ClientConfig clientConfig = clientConfig(config);
    RemoteWebDriver webDriver = new RemoteWebDriver(requireNonNull(clientConfig.baseUrl()), capabilities, clientConfig);
    webDriver.setFileDetector(new LocalFileDetector());
    if (cdpEnabled != null && webDriver.getCapabilities() instanceof MutableCapabilities webdriverCapabilities) {
      webdriverCapabilities.setCapability("se:cdpEnabled", cdpEnabled);
    }

    return new Augmenter().augment(webDriver);
  }

  /**
   * Credentials are moved from the URL to {@link ClientConfig#authenticateAs},
   * because Selenium sends the base URL to the remote server in "se:remoteUrl" capability.
   */
  ClientConfig clientConfig(Config config) {
    URI remoteUri = remoteUri(config);
    ClientConfig clientConfig = ClientConfig.defaultConfig()
      .baseUri(withoutUserInfo(remoteUri))
      .readTimeout(Duration.ofMillis(config.remoteReadTimeout()))
      .connectionTimeout(Duration.ofMillis(config.remoteConnectionTimeout()));

    String userInfo = remoteUri.getUserInfo();
    return userInfo == null || userInfo.isBlank() ? clientConfig : clientConfig.authenticateAs(credentials(userInfo));
  }

  private URI remoteUri(Config config) {
    String remoteUrl = requireNonNull(config.remote(), "Remote browser URL is not configured");
    try {
      return new URL(remoteUrl).toURI();
    }
    catch (MalformedURLException | URISyntaxException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remoteUrl, e);
    }
  }

  private URI withoutUserInfo(URI uri) {
    String rawUserInfo = uri.getRawUserInfo();
    return rawUserInfo == null ? uri : URI.create(uri.toString().replaceFirst("//" + quote(rawUserInfo) + "@", "//"));
  }

  private UsernameAndPassword credentials(String userInfo) {
    String[] parts = userInfo.split(":", 2);
    return new UsernameAndPassword(parts[0], parts.length > 1 ? parts[1] : "");
  }
}
