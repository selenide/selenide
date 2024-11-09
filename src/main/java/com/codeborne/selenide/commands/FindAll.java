package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.WebElementSource;

import static com.codeborne.selenide.commands.Util.firstOf;

public class FindAll implements Command<ElementsCollection> {
  @Override
  public ElementsCollection execute(SelenideElement parentElement, WebElementSource parentLocator, Object... args) {
    Object selector = firstOf(args);
    return new ElementsCollection(
      new BySelectorCollection(parentLocator.driver(), parentLocator, WebElementSource.getSelector(selector))
    );
  }
}
