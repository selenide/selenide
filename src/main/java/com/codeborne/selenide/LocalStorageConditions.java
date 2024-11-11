package com.codeborne.selenide;

import com.codeborne.selenide.conditions.localstorage.Item;
import com.codeborne.selenide.conditions.localstorage.ItemWithValue;

public class LocalStorageConditions {

  public static ObjectCondition<LocalStorage> item(String item) {
    return new Item(item);
  }

  public static ObjectCondition<LocalStorage> itemWithValue(String item, String value) {
    return new ItemWithValue(item, value);
  }
}
