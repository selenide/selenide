package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.impl.ElementFinder;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import static com.codeborne.selenide.commands.Util.firstOf;

public class AppiumFind implements Command<SelenideAppiumElement> {
  @Override
  public SelenideAppiumElement execute(SelenideElement parentElement, WebElementSource parentLocator, Object... args) {
    Object selector = firstOf(args);
    By by = WebElementSource.getSelector(selector);
    int index = args.length > 1 ? (Integer) args[1] : 0;
    return ElementFinder.wrap(parentLocator.driver(), SelenideAppiumElement.class, parentLocator, by, index);
  }
}
