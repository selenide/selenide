package com.codeborne.selenide.conditions.localstorage;

import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Item implements ObjectCondition<LocalStorage> {
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

  @Nullable
  @CheckReturnValue
  @Override
  public String actualValue(LocalStorage localStorage) {
    return localStorage.getItems().toString();
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return item;
  }

  @CheckReturnValue
  @Override
  public boolean test(LocalStorage localStorage) {
    return localStorage.containsItem(item);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(LocalStorage localStorage) {
    return "localStorage";
  }
}
