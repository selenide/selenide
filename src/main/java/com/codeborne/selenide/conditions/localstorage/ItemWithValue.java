package com.codeborne.selenide.conditions.localstorage;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.ObjectCondition;

import java.util.Objects;

public class ItemWithValue implements ObjectCondition<LocalStorage> {
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

  private String actualValue(LocalStorage localStorage) {
    return localStorage.getItems().toString();
  }

  @Override
  public String expectedValue() {
    return String.format("%s=%s", item, value);
  }

  @Override
  public CheckResult check(LocalStorage localStorage) {
    boolean met = Objects.equals(localStorage.getItem(item), value);
    return result(localStorage, met, actualValue(localStorage));
  }

  @Override
  public String describe(LocalStorage localStorage) {
    return "localStorage";
  }
}
