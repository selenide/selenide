package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

class OperaDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isOpera;
  private final DriverProcessor nextProcessor;

  OperaDriverProcessor() {
    this.nextProcessor = new SafariDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createOperaDriver(proxy) : nextProcessor.process(proxy);
  }

  private WebDriver createOperaDriver(final Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.opera.OperaDriver", proxy);
  }
}
