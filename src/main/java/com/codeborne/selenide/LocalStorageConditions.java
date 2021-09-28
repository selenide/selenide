package com.codeborne.selenide;

import com.codeborne.selenide.conditions.localstorage.Item;
import com.codeborne.selenide.conditions.localstorage.ItemWithValue;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * @since 5.23.0
 */
public class LocalStorageConditions {

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<LocalStorage> item(String item) {
    return new Item(item);
  }

  @CheckReturnValue
  @Nonnull
  public static ObjectCondition<LocalStorage> itemWithValue(String item, String value) {
    return new ItemWithValue(item, value);
  }
}
