package com.codeborne.selenide;

import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.lang.Integer.parseInt;

@ParametersAreNonnullByDefault
abstract class JSStorage {
  private final Driver driver;
  private final String storage;

  JSStorage(Driver driver, String storage) {
    this.driver = driver;
    this.storage = storage;
  }

  @Nonnull
  @CheckReturnValue
  public Driver driver() {
    return driver;
  }

  @CheckReturnValue
  public boolean containsItem(String key) {
    return getItem(key) != null;
  }

  @CheckReturnValue
  @Nullable
  public String getItem(String key) {
    return driver.executeJavaScript(js("return %s.getItem(arguments[0])"), key);
  }

  public void setItem(String key, String value) {
    SelenideLog log = SelenideLogger.beginStep(toString(), "setItem", key, value);
    driver.executeJavaScript(js("%s.setItem(arguments[0], arguments[1])"), key, value);
    SelenideLogger.commitStep(log, PASS);
  }

  public void removeItem(String key) {
    SelenideLog log = SelenideLogger.beginStep(toString(), "removeItem");
    driver.executeJavaScript(js("%s.removeItem(arguments[0])"), key);
    SelenideLogger.commitStep(log, PASS);
  }

  public void clear() {
    SelenideLog log = SelenideLogger.beginStep(toString(), "clear");
    driver.executeJavaScript(js("%s.clear()"));
    SelenideLogger.commitStep(log, PASS);
  }

  @CheckReturnValue
  public int size() {
    return parseInt(driver.executeJavaScript(js("return %s.length")).toString());
  }

  @CheckReturnValue
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public String toString() {
    return storage;
  }

  /**
   * @return all items in this storage
   * @since 5.23.0
   */
  @CheckReturnValue
  @Nonnull
  public Map<String, String> getItems() {
    return driver.executeJavaScript(js("""
      return Object.keys(%1$s).reduce((items, key) => {
         items[key] = %1$s.getItem(key);
         return items;
      }, {});"""));
  }

  private String js(String jsCodeTemplate) {
    return String.format(jsCodeTemplate, storage);
  }
}
