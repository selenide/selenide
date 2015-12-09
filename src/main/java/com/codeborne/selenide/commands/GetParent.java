package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

public class GetParent implements Command<SelenideElement> {
  Find find = new Find();
  
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    return find.execute(proxy, locator, By.xpath(".."), 0);
  }
}
