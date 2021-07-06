package com.codeborne.selenide.conditions.sessionstorage;

import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SessionStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ItemWithValue implements ObjectCondition<SessionStorage> {
  private final String item;
  private final String value;

  public ItemWithValue(String item, String value) {
    this.item = item;
    this.value = value;
  }

  @Nonnull
  @Override
  public String description() {
    return String.format("sessionStorage should have item '%s' with value '%s'", item, value);
  }

  @Nullable
  @Override
  public Object actualValue(SessionStorage sessionStorage) {
    return sessionStorage.getItems();
  }

  @Override
  public boolean test(SessionStorage sessionStorage) {
    return Objects.equals(sessionStorage.getItem(item), value);
  }
}
