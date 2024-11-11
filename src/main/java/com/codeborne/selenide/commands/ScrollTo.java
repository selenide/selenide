package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Point;

public class ScrollTo extends FluentCommand {
  @Override
  public void execute(WebElementSource locator, Object @Nullable [] args) {
    Point location = locator.getWebElement().getLocation();
    locator.driver().executeJavaScript("window.scrollTo(" + location.getX() + ", " + location.getY() + ')');
  }
}
