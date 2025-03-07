package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.ContextAware;

import java.util.Set;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;

public class SelenideAppiumTargetLocator {
  private final Driver driver;

  SelenideAppiumTargetLocator(Driver driver) {
    this.driver = driver;
  }

  public void context(String contextName) {
    SelenideLogger.run("set context", contextName, () -> {
      cast(driver, ContextAware.class)
        .map(contextAware -> contextAware.context(contextName))
        .orElseThrow(() -> new UnsupportedOperationException("Context not found" + contextName));
    });
  }

  public Set<String> getContextHandles() {
    return cast(driver, ContextAware.class)
      .map(ContextAware::getContextHandles)
      .orElseThrow(() -> new UnsupportedOperationException("Cannot get contexts from mobile driver"));
  }

  public String getCurrentContext() {
    return cast(driver, ContextAware.class)
      .map(ContextAware::getContext)
      .orElseThrow(() -> new UnsupportedOperationException("Cannot get current context from mobile driver"));
  }
}
