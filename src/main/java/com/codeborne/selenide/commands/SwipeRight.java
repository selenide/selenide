package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class SwipeRight extends Swipe {
  @Override
  protected int getSwipingXDistance(SelenideElement proxy, WebElementSource locator) {
    int windowWidth = locator.driver().getWebDriver().manage().window().getSize().getWidth();
    int swipingDistance = windowWidth - (proxy.getLocation().getX() + proxy.getSize().getWidth() / 2);
    return swipingDistance;
  }
}
