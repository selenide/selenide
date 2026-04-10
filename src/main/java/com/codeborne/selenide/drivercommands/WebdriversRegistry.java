package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.WebDriverInstance;

import java.util.Optional;

public class WebdriversRegistry {
  private static final DisposablesRegistry<Long, WebDriverInstance> registry = new DisposablesRegistry<>();

  public static void register(WebDriverInstance driver) {
    registry.register(driver.threadId(), driver);
  }

  public static void unregister(WebDriverInstance driver) {
    registry.unregister(driver.threadId());
  }

  public static Optional<WebDriverInstance> webdriver(long threadId) {
    return registry.get(threadId);
  }
}
