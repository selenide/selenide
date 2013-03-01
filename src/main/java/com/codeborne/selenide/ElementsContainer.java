package com.codeborne.selenide;

public abstract class ElementsContainer {
  private SelenideElement self;

  public ElementsContainer() {

  }

  public SelenideElement getSelf() {
    return self;
  }

  public void setSelf(SelenideElement self) {
    this.self = self;
  }
}
