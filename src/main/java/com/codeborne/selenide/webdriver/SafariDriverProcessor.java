package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

class SafariDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isSafari;
  private final DriverProcessor nextProcessor;

  SafariDriverProcessor() {
    this.nextProcessor = new JBrowserDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createSafariDriver(proxy) : nextProcessor.process(proxy);
  }

  private WebDriver createSafariDriver(final Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.safari.SafariDriver", proxy);
  }
}
