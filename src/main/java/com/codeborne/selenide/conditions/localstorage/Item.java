package com.codeborne.selenide.conditions.localstorage;

import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Item implements ObjectCondition<LocalStorage> {
  private final String item;

  public Item(String item) {
    this.item = item;
  }

  @Nonnull
  @Override
  public String description() {
    return String.format("localStorage should have item '%s'", item);
  }

  @Nullable
  @Override
  public Object actualValue(LocalStorage localStorage) {
    return localStorage.getItems();
  }

  @Override
  public boolean test(LocalStorage localStorage) {
    return localStorage.containsItem(item);
  }
}
