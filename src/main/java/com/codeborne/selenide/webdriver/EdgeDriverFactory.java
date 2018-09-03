package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserBinary;

class EdgeDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(EdgeDriverFactory.class.getName());

  @Override
  WebDriver create(final Proxy proxy) {
    return createEdgeDriver(proxy);
  }

  @Override
  boolean supports(Browser browser) {
    return browser.isEdge();
  }

  private WebDriver createEdgeDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    EdgeOptions options = new EdgeOptions();
    options.merge(capabilities);
    if (!browserBinary.isEmpty()) {
      log.info("Using browser binary: " + browserBinary);
      log.warning("Changing browser binary not supported in Edge, setting will be ignored.");
    }
    return new EdgeDriver(options);
  }
}
