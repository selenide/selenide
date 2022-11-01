package com.codeborne.selenide.appium;

import com.codeborne.selenide.ClickMethod;
import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.appium.commands.AppiumClickMethod;

public class AppiumClickOptions extends ClickOptions {

  private final AppiumClickMethod appiumClickMethod;
  private final int xOffset;
  private final int yOffset;

  private AppiumClickOptions(AppiumClickMethod appiumClickMethod, int xOffSet, int yOffset){
    super(ClickMethod.DEFAULT, 0, 0, null);
    this.appiumClickMethod = appiumClickMethod;
    this.xOffset = xOffSet;
    this.yOffset = yOffset;
  }

  public static AppiumClickOptions tap(){
    return new AppiumClickOptions(AppiumClickMethod.TAP, 0, 0);
  }

  public static AppiumClickOptions tapWithOffset(int xOffset, int yOffset){
    return new AppiumClickOptions(AppiumClickMethod.TAP_WITH_OFFSET, xOffset, yOffset);
  }

  public static AppiumClickOptions doubleTap(){
    return new AppiumClickOptions(AppiumClickMethod.DOUBLE_TAP, 0, 0);
  }

  public static AppiumClickOptions longPress(){
    return new AppiumClickOptions(AppiumClickMethod.LONG_PRESS, 0, 0);
  }

  public AppiumClickMethod appiumClickMethod(){
    return appiumClickMethod;
  }

  @Override
  public int offsetX(){
    return xOffset;
  }

  @Override
  public int offsetY(){
    return yOffset;
  }

}
