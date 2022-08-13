package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.WebDriverInstance;

public class WebdriversRegistry {
  private static final DisposablesRegistry<WebDriverInstance> registry = new DisposablesRegistry<>();

  public static void register(WebDriverInstance driver) {
    registry.register(driver);
  }

  public static void unregister(WebDriverInstance driver) {
    registry.unregister(driver);
  }
}
