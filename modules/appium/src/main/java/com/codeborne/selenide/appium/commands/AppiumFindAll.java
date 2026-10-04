package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import com.codeborne.selenide.impl.WebElementSource;

import static com.codeborne.selenide.commands.Util.firstOf;

public class AppiumFindAll implements Command<SelenideAppiumCollection> {
  @Override
  public SelenideAppiumCollection execute(SelenideElement parentElement, WebElementSource parentLocator, Object... args) {
    Object selector = firstOf(args);
    return new SelenideAppiumCollection(parentLocator, WebElementSource.getSelector(selector));
  }
}
