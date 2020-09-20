package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

public class LocalStorage {
  private final Driver driver;

  LocalStorage(Driver driver) {
    this.driver = driver;
  }

  @CheckReturnValue
  @Nullable
  public String getItem(String key) {
    return driver.executeJavaScript("return localStorage.getItem(arguments[0])", key);
  }

  public void setItem(String key, String value) {
    driver.executeJavaScript("localStorage.setItem(arguments[0], arguments[1])", key, value);
  }

  public void removeItem(String key) {
    driver.executeJavaScript("localStorage.removeItem(arguments[0])", key);
  }

  public void clear() {
    driver.executeJavaScript("localStorage.clear()");
  }

  public int size() {
    long size = driver.executeJavaScript("return localStorage.length");
    return (int) size;
  }
}
