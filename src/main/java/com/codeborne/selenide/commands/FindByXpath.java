package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

public class FindByXpath implements Command<SelenideElement> {

  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object... args) {
    By byXpath = By.xpath((String) args[0]);
    return args.length == 1 ?
        locator.find(proxy, byXpath, 0) :
        locator.find(proxy, byXpath, (Integer) args[1]);
  }

}
