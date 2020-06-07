package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class ElementsContainer {
  private SelenideElement self;

  public ElementsContainer() {

  }

  @CheckReturnValue
  @Nonnull
  public SelenideElement getSelf() {
    return self;
  }

  public void setSelf(SelenideElement self) {
    this.self = self;
  }
}
