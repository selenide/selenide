package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import static java.lang.String.format;

public class GetClosest implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String tagOrClass = (String) args[0];
    String xpath = tagOrClass.startsWith(".") ?
        format("ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][1]", tagOrClass.substring(1)) :
        format("ancestor::%s[1]", tagOrClass);
    return locator.find(proxy, By.xpath(xpath), 0);
  }
}
