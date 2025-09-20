package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.remote.SupportsContextSwitching;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class SelenideAppiumTargetLocator {
  private final Driver driver;

  SelenideAppiumTargetLocator(Driver driver) {
    this.driver = driver;
  }

  public void context(String contextName) {
    SelenideLogger.run("set context", contextName, () -> {
      SupportsContextSwitching contextAware = (SupportsContextSwitching) driver.getWebDriver();
      contextAware.context(contextName);
    });
  }

  public Set<String> getContextHandles() {
    SupportsContextSwitching contextAware = (SupportsContextSwitching) driver.getWebDriver();
    return contextAware.getContextHandles();
  }

  @Nullable
  public String getCurrentContext() {
    SupportsContextSwitching contextAware = (SupportsContextSwitching) driver.getWebDriver();
    return contextAware.getContext();
  }
}
