package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SessionStorage extends JSStorage implements Conditional<SessionStorage> {
  SessionStorage(Driver driver) {
    super(driver, "sessionStorage");
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public SessionStorage object() {
    return this;
  }
}
