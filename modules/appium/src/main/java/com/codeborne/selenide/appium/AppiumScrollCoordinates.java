package com.codeborne.selenide.appium;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppiumScrollCoordinates {

  private final int startX;
  private final int startY;
  private final int endX;
  private final int endY;

  public AppiumScrollCoordinates(int startX, int startY, int endX, int endY) {
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
  }

  @CheckReturnValue
  public int getStartX() {
    return startX;
  }

  @CheckReturnValue
  public int getStartY() {
    return startY;
  }

  @CheckReturnValue
  public int getEndX() {
    return endX;
  }

  @CheckReturnValue
  public int getEndY() {
    return endY;
  }
}
