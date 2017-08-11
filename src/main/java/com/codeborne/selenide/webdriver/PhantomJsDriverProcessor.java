package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

class PhantomJsDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isPhantomjs;
  private final DriverProcessor nextProcessor;

  PhantomJsDriverProcessor() {
    this.nextProcessor = new OperaDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createPhantomJsDriver(proxy) : nextProcessor.process(proxy);
  }

  private WebDriver createPhantomJsDriver(final Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver", proxy);
  }
}
