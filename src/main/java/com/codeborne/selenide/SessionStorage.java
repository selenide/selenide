package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SessionStorage extends JSStorage {
  SessionStorage(Driver driver) {
    super(driver, "sessionStorage");
  }
}
