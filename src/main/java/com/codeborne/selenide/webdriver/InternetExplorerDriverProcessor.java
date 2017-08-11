package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.function.Supplier;

class InternetExplorerDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isIE;
  private final DriverProcessor nextProcessor;

  InternetExplorerDriverProcessor() {
    this.nextProcessor = new PhantomJsDriverProcessor();
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
