package com.codeborne.selenide;

public class SessionStorage extends JSStorage implements Conditional<SessionStorage> {
  SessionStorage(Driver driver) {
    super(driver, "sessionStorage");
  }

  @Override
  public SessionStorage object() {
    return this;
  }
}
