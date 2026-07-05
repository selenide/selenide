package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;

import static com.codeborne.selenide.commands.Util.firstOf;

public class AppiumFindAllByXpath implements Command<SelenideAppiumCollection> {
  @Override
  public SelenideAppiumCollection execute(SelenideElement parentElement, WebElementSource parentLocator, Object @Nullable [] args) {
    String xpath = firstOf(args);
    return new SelenideAppiumCollection(parentLocator, By.xpath(xpath));
  }
}
