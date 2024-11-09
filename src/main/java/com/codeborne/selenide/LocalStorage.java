package com.codeborne.selenide;

public class LocalStorage extends JSStorage implements Conditional<LocalStorage> {
  LocalStorage(Driver driver) {
    super(driver, "localStorage");
  }

  @Override
  public LocalStorage object() {
    return this;
  }
}
