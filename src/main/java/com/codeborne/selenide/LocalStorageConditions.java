package com.codeborne.selenide;

import com.codeborne.selenide.conditions.localstorage.Item;
import com.codeborne.selenide.conditions.localstorage.ItemWithValue;

/**
 * @since 5.23.0
 */
public class LocalStorageConditions {

  public static ObjectCondition<LocalStorage> item(String item) {
    return new Item(item);
  }

  public static ObjectCondition<LocalStorage> itemWithValue(String item, String value) {
    return new ItemWithValue(item, value);
  }
}
