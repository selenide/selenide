package com.codeborne.selenide.conditions.sessionstorage;

import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SessionStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Item implements ObjectCondition<SessionStorage> {
  private final String item;

  public Item(String item) {
    this.item = item;
  }

  @Nonnull
  @Override
  public String description() {
    return String.format("sessionStorage should have item '%s'", item);
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return String.format("sessionStorage should not have item '%s'", item);
  }

  @Nullable
  @Override
  public Object actualValue(SessionStorage sessionStorage) {
    return sessionStorage.getItems();
  }

  @Override
  public boolean test(SessionStorage sessionStorage) {
    return sessionStorage.containsItem(item);
  }
}
