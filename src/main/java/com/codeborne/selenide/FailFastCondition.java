package com.codeborne.selenide;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

import static java.lang.String.format;

/**
 * Fail fast condition - specifies what condition should interrupt waiting
 * in `should` methods once it triggers.
 * */
public class FailFastCondition {
  private final BooleanSupplier condition;
  private final String description;

  public FailFastCondition(@Nonnull BooleanSupplier condition, @Nonnull String description) {
    this.condition = condition;
    this.description = description;
  }

  @Override
  public String toString() {
    return format("Fail fast condition has triggered: %s", description);
  }

  public Boolean getAsBoolean() {
    return condition.getAsBoolean();
  }
}
