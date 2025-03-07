package com.codeborne.selenide.conditions.sessionstorage;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SessionStorage;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ItemWithValue implements ObjectCondition<SessionStorage> {
  private final String item;
  private final String value;

  public ItemWithValue(String item, String value) {
    this.item = item;
    this.value = value;
  }

  @Override
  public String description() {
    return String.format("should have item '%s' with value '%s'", item, value);
  }

  @Override
  public String negativeDescription() {
    return String.format("should not have item '%s' with value '%s'", item, value);
  }

  private String actualValue(SessionStorage sessionStorage) {
    return sessionStorage.getItems().toString();
  }

  @Override
  @Nullable
  public String expectedValue() {
    return String.format("%s=%s", item, value);
  }

  @Override
  public CheckResult check(SessionStorage sessionStorage) {
    String itemValue = sessionStorage.getItem(item);
    return result(sessionStorage, Objects.equals(itemValue, value), actualValue(sessionStorage));
  }

  @Override
  public String describe(SessionStorage sessionStorage) {
    return "sessionStorage";
  }
}
