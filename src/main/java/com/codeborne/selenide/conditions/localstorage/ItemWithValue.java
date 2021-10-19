package com.codeborne.selenide.conditions.localstorage;

import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ItemWithValue implements ObjectCondition<LocalStorage> {
  private final String item;
  private final String value;

  public ItemWithValue(String item, String value) {
    this.item = item;
    this.value = value;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return String.format("should have item '%s' with value '%s'", item, value);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return String.format("should not have item '%s' with value '%s'", item, value);
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
    return String.format("%s=%s", item, value);
  }

  @CheckReturnValue
  @Override
  public boolean test(LocalStorage localStorage) {
    return Objects.equals(localStorage.getItem(item), value);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(LocalStorage localStorage) {
    return "localStorage";
  }
}
