package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LocalStorage extends JSStorage {
  LocalStorage(Driver driver) {
    super(driver, "localStorage");
  }
}
