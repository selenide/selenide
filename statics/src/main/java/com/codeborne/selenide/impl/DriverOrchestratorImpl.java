package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;

import java.util.concurrent.Callable;

@SuppressWarnings("unused")
public class DriverOrchestratorImpl implements DriverOrchestrator {
  @Override
  public <V> V using(Driver driver, Callable<V> r) throws Exception {
    return WebDriverRunner.using(driver, r);
  }
}
