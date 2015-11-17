package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.actions;

public class DragAndDropTo implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String targetCssSelector = (String) args[0];
    SelenideElement target = $(targetCssSelector).shouldBe(visible);
    actions().dragAndDrop(locator.getWebElement(), target).perform();
    return proxy;
  }
}
