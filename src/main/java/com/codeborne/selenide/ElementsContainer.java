package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class ElementsContainer {
  private SelenideElement self;

  /**
   * @deprecated I rather think that this method is not needed.
   * You are expected to find elements INSIDE this container, not the container itself.
   */
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public SelenideElement getSelf() {
    return self;
  }
}
