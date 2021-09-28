package com.codeborne.selenide.conditions.sessionstorage;

import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SessionStorage;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ItemWithValue implements ObjectCondition<SessionStorage> {
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
  public String actualValue(SessionStorage sessionStorage) {
    return sessionStorage.getItems().toString();
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return String.format("%s=%s", item, value);
  }

  @CheckReturnValue
  @Override
  public boolean test(SessionStorage sessionStorage) {
    return Objects.equals(sessionStorage.getItem(item), value);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(SessionStorage sessionStorage) {
    return "sessionStorage";
  }
}
