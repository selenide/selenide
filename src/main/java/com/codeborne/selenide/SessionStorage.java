package com.codeborne.selenide;

import static java.lang.Integer.parseInt;
import static java.util.Optional.ofNullable;

/**
 * Created by dbudim on 10.02.2021
 */

public class SessionStorage {

  private final Driver driver;

  SessionStorage(Driver driver) {
    this.driver = driver;
  }

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

  public int size() {
    return parseInt(driver.executeJavaScript("return sessionStorage.length").toString());
  }

  public boolean isEmpty() {
    return size() == 0;
  }

}
