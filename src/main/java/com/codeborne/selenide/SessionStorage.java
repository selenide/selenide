package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import static java.lang.Integer.parseInt;
import static java.util.Optional.ofNullable;

public class SessionStorage {

  private final Driver driver;

  SessionStorage(Driver driver) {
    this.driver = driver;
  }

  @Nonnull
  @CheckReturnValue
  public boolean containsItem(String key) {
    return ofNullable(getItem(key)).isPresent();
  }

  public String getItem(String key) {
    return driver.executeJavaScript("return sessionStorage.getItem(arguments[0])", key);
  }

  public void setItem(String key, String value) {
    driver.executeJavaScript("sessionStorage.setItem(arguments[0], arguments[1])", key, value);
  }

  public void removeItem(String key) {
    driver.executeJavaScript("sessionStorage.removeItem(arguments[0])", key);
  }

  public void clear() {
    driver.executeJavaScript("sessionStorage.clear()");
  }

  @CheckReturnValue
  public int size() {
    return parseInt(driver.executeJavaScript("return sessionStorage.length").toString());
  }

  @CheckReturnValue
  public boolean isEmpty() {
    return size() == 0;
  }

}
