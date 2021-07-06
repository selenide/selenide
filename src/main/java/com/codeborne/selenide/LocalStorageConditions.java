package com.codeborne.selenide;

import java.util.function.Predicate;

public class LocalStorageConditions {

  public static Predicate<LocalStorage> containItem(String item) {
    return localStorage -> localStorage.containsItem(item);
  }

  public static Predicate<LocalStorage> containItemWithValue(String item, String value) {
    return localStorage -> localStorage.getItem(item).equals(value);
  }

}
