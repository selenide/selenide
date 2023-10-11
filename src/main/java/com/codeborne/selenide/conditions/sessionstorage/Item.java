package com.codeborne.selenide.conditions.sessionstorage;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SessionStorage;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Item implements ObjectCondition<SessionStorage> {
  private final String item;

  public Item(String item) {
    this.item = item;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return String.format("should have item '%s'", item);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return String.format("should not have item '%s'", item);
  }

  @Nonnull
  @CheckReturnValue
  private String actualValue(SessionStorage sessionStorage) {
    return sessionStorage.getItems().toString();
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return item;
  }

  @CheckReturnValue
  @Override
  public CheckResult check(SessionStorage sessionStorage) {
    boolean met = sessionStorage.containsItem(item);
    return result(sessionStorage, met, actualValue(sessionStorage));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(SessionStorage sessionStorage) {
    return "sessionStorage";
  }
}
