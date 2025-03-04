package com.codeborne.selenide.conditions.localstorage;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.ObjectCondition;

public class Item implements ObjectCondition<LocalStorage> {
  private final String item;

  public Item(String item) {
    this.item = item;
  }

  @Override
  public String description() {
    return String.format("should have item '%s'", item);
  }

  @Override
  public String negativeDescription() {
    return String.format("should not have item '%s'", item);
  }

  private String actualValue(LocalStorage localStorage) {
    return localStorage.getItems().toString();
  }

  @Override
  public String expectedValue() {
    return item;
  }

  @Override
  public CheckResult check(LocalStorage localStorage) {
    return result(localStorage, localStorage.containsItem(item), actualValue(localStorage));
  }

  @Override
  public String describe(LocalStorage localStorage) {
    return "localStorage";
  }
}
