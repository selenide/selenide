package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

class JBrowserDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isJBrowser;
  private final DriverProcessor nextProcessor;

  JBrowserDriverProcessor() {
    this.nextProcessor = new DefaultDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createJBrowserDriver(proxy) : nextProcessor.process(proxy);
  }

  private WebDriver createJBrowserDriver(final Proxy proxy) {
    return createInstanceOf("com.machinepublishers.jbrowserdriver.JBrowserDriver", proxy);
  }
}
