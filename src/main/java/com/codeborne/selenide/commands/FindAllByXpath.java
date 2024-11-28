package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;

import static com.codeborne.selenide.commands.Util.firstOf;

public class FindAllByXpath implements Command<ElementsCollection> {

  @Override
  public ElementsCollection execute(SelenideElement parentElement, WebElementSource parentLocator, Object @Nullable [] args) {
    String xpath = firstOf(args);
    return new ElementsCollection(new BySelectorCollection(parentLocator.driver(), parentLocator, By.xpath(xpath)));
  }
}
