package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

public class GetPreceding implements Command<SelenideElement> {

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    int siblingIndex = (int) args[0] + 1;
    return locator.find(proxy, By.xpath(String.format("preceding-sibling::*[%d]", siblingIndex)), 0);
  }
}
