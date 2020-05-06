package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;

import java.util.concurrent.Callable;

public interface DriverOrchestrator {
  <V> V using(Driver driver, Callable<V> r) throws Exception;
}
