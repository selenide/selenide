package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class ScrollTo implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    Point location = locator.getWebElement().getLocation();
    locator.driver().executeJavaScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ')');
    return proxy;
  }
}
