package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class SwipeLeft extends Swipe {
  @Override
  protected int getSwipingXDistance(SelenideElement proxy, WebElementSource locator) {
    int swipingDistance = proxy.getLocation().getX() + proxy.getSize().getWidth() / 2;
    return -swipingDistance;
  }
}
