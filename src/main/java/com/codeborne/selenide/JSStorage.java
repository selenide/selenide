package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

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
    driver.executeJavaScript(js("%s.setItem(arguments[0], arguments[1])"), key, value);
  }

  public void removeItem(String key) {
    driver.executeJavaScript(js("%s.removeItem(arguments[0])"), key);
  }

  public void clear() {
    driver.executeJavaScript(js("%s.clear()"));
  }

  @CheckReturnValue
  public int size() {
    return parseInt(driver.executeJavaScript(js("return %s.length")).toString());
  }

  @CheckReturnValue
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * @return all items in this storage
   * @since 5.23.0
   */
  @CheckReturnValue
  public Map<String, String> getItems() {
    return driver.executeJavaScript(js("return Object.keys(%1$s).reduce((items, key) => {\n" +
      "   items[key] = %1$s.getItem(key);\n" +
      "   return items\n" +
      "}, {});"));
  }

  private String js(String jsCodeTemplate) {
    return String.format(jsCodeTemplate, storage);
  }
}
