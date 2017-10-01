package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

class EdgeDriverFactory extends AbstractDriverFactory {

  @Override
  WebDriver create(final Proxy proxy) {
    return createInternetExplorerDriver(proxy);
  }

  @Override
  boolean supports() {
    return WebDriverRunner.isEdge();
  }

  private WebDriver createInternetExplorerDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    EdgeOptions options = new EdgeOptions();
    options.merge(capabilities);
    return new EdgeDriver(options);
  }
}
