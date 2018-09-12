package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.WebElementSource;

public class FindAll implements Command<ElementsCollection> {
  @Override
  public ElementsCollection execute(SelenideElement parent, WebElementSource locator, Object[] args) {
    return new ElementsCollection(new BySelectorCollection(locator.driver(), parent, WebElementSource.getSelector(args[0])));
  }
}
