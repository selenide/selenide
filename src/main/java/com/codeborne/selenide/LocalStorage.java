package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LocalStorage extends JSStorage implements Conditional<LocalStorage> {
  LocalStorage(Driver driver) {
    super(driver, "localStorage");
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public LocalStorage object() {
    return this;
  }
}
