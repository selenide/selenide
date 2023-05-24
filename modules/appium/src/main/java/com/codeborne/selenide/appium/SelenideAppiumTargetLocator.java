package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.ContextAware;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
public class SelenideAppiumTargetLocator {
  private final Driver driver;

  SelenideAppiumTargetLocator(Driver driver) {
    this.driver = driver;
  }

  public void context(String contextName) {
    SelenideLogger.run("set context", contextName, () -> {
      WebdriverUnwrapper.cast(driver, ContextAware.class)
        .map(contextAware -> contextAware.context(contextName))
        .orElseThrow(() -> new UnsupportedOperationException("Context not found" + contextName));
    });
  }

  public Set<String> getContextHandles() {
    return WebdriverUnwrapper.cast(driver, ContextAware.class)
      .map(ContextAware::getContextHandles)
      .orElseThrow(() -> new UnsupportedOperationException("Cannot get contexts from mobile driver"));
  }

  public String getCurrentContext() {
    return WebdriverUnwrapper.cast(driver, ContextAware.class)
      .map(ContextAware::getContext)
      .orElseThrow(() -> new UnsupportedOperationException("Cannot get current context from mobile driver"));
  }
}
