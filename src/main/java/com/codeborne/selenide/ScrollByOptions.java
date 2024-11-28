package com.codeborne.selenide;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ScrollByDirection.DOWN;

public class ScrollByOptions {
  private final ScrollByDirection scrollByDirection;
  private final int distance;
  private static final ScrollByDirection DEFAULT_SCROLL_DIRECTION = DOWN;
  private static final int DEFAULT_SCROLL_LENGHT = 1000;

  protected ScrollByOptions(ScrollByDirection scrollByDirection, int distance) {
    this.scrollByDirection = scrollByDirection;
    this.distance = distance;
  }

  public static ScrollByOptions defaultScrollByOptions() {
    return new ScrollByOptions(DEFAULT_SCROLL_DIRECTION, DEFAULT_SCROLL_LENGHT);
  }

  public static ScrollByOptions direction(ScrollByDirection scrollByDirection) {
    return new ScrollByOptions(scrollByDirection, DEFAULT_SCROLL_LENGHT);
  }

  public ScrollByOptions distance(int distance) {
    if (distance >= 0)
      return new ScrollByOptions(scrollByDirection, distance);
    else
      throw new IllegalArgumentException("The distance should be a positive number");
  }

  public ScrollByDirection getDirection() {
    return this.scrollByDirection;
  }

  public int getDistance() {
    return this.distance;
  }
}
