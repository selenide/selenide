package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class ScrollTo implements Command<WebElement> {
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    Point location = locator.findAndAssertElementIsVisible().getLocation();
    executeJavaScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ')');
    return proxy;
  }
}
