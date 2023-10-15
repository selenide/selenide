package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @deprecated use interface {@link Container} instead of abstract class {@link ElementsContainer}
 */
@Deprecated
@ParametersAreNonnullByDefault
public abstract class ElementsContainer implements Container {
  private SelenideElement self;

  /**
   * @deprecated You don't really need to get "self" element.
   * Instead, you need to access fields (web elements) of this class.
   */
  @CheckReturnValue
  @Nonnull
  @Deprecated
  public SelenideElement getSelf() {
    return self;
  }
}
