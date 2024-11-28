package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementSource;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Command<T> {
  Object[] NO_ARGS = new Object[0];

  @Nullable
  @CanIgnoreReturnValue
  @SuppressWarnings("NullableProblems") // some children can return null, some cannot.
  T execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args);
}
