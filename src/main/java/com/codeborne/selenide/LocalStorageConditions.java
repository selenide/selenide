package com.codeborne.selenide;

import java.util.Objects;
import java.util.function.Predicate;

public class LocalStorageConditions {

  public static Predicate<LocalStorage> containItem(String item) {
    return localStorage -> localStorage.containsItem(item);
  }

  public static Predicate<LocalStorage> containItemWithValue(String item, String value) {
    return localStorage -> Objects.equals(localStorage.getItem(item), value);
  }
}
