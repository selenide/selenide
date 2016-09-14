package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.ExBy;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;

public class GetParent implements Command<SelenideElement> {
  Find find = new Find();
  
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    return find.execute(proxy, locator, byXpath(".."), 0);
  }
}
