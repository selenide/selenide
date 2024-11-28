package com.codeborne.selenide.conditions.sessionstorage;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SessionStorage;
import org.jspecify.annotations.Nullable;

public class Item implements ObjectCondition<SessionStorage> {
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

  private String actualValue(SessionStorage sessionStorage) {
    return sessionStorage.getItems().toString();
  }

  @Override
  @Nullable
  public String expectedValue() {
    return item;
  }

  @Override
  public CheckResult check(SessionStorage sessionStorage) {
    boolean met = sessionStorage.containsItem(item);
    return result(sessionStorage, met, actualValue(sessionStorage));
  }

  @Override
  public String describe(SessionStorage sessionStorage) {
    return "sessionStorage";
  }
}
