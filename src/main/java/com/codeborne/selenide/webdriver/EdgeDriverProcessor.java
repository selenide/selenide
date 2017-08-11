package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.function.Supplier;

class EdgeDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isEdge;
  private final DriverProcessor nextProcessor;

  EdgeDriverProcessor() {
    this.nextProcessor = new InternetExplorerDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createInternetExplorerDriver(proxy) : nextProcessor.process(proxy);
  }

  private WebDriver createInternetExplorerDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    return new InternetExplorerDriver(capabilities);
  }
}
