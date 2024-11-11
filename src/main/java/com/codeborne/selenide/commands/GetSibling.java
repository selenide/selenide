package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;

import static com.codeborne.selenide.commands.Util.firstOf;

public class GetSibling implements Command<SelenideElement> {

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    int siblingIndex = (int) firstOf(args) + 1;
    return locator.find(proxy, By.xpath(String.format("following-sibling::*[%d]", siblingIndex)), 0);
  }
}
