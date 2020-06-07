package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public interface Command<T> {
  Object[] NO_ARGS = new Object[0];

  @Nullable
  T execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException;
}
