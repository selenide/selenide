package com.codeborne.selenide;

import com.codeborne.selenide.conditions.sessionstorage.Item;
import com.codeborne.selenide.conditions.sessionstorage.ItemWithValue;

public class SessionStorageConditions {

  public static ObjectCondition<SessionStorage> item(String item) {
    return new Item(item);
  }

  public static ObjectCondition<SessionStorage> itemWithValue(String item, String value) {
    return new ItemWithValue(item, value);
  }
}
