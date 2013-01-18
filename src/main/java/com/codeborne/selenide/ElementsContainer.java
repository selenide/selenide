package com.codeborne.selenide;

public abstract class ElementsContainer {
  private ShouldableWebElement self;

  public ElementsContainer() {

  }

  public ShouldableWebElement getSelf() {
    return self;
  }

  public void setSelf(ShouldableWebElement self) {
    this.self = self;
  }
}
