package com.codeborne.selenide;

import static com.codeborne.selenide.ScrollDirection.DOWN;

public class ScrollOptions {
  private final ScrollDirection scrollDirection;
  private final int distance;
  private static final ScrollDirection DEFAULT_SCROLL_DIRECTION = DOWN;
  private static final int DEFAULT_SCROLL_LENGHT = 1000;

  protected ScrollOptions(ScrollDirection scrollDirection, int distance) {
    this.scrollDirection = scrollDirection;
    this.distance = distance;
  }

  public static ScrollOptions defaultScrollOptions() {
    return new ScrollOptions(DEFAULT_SCROLL_DIRECTION, DEFAULT_SCROLL_LENGHT);
  }

  public static ScrollOptions direction(ScrollDirection scrollDirection) {
    return new ScrollOptions(scrollDirection, DEFAULT_SCROLL_LENGHT);
  }

  public ScrollOptions distance(int distance) {
    if (distance >= 0)
      return new ScrollOptions(scrollDirection, distance);
    else
      throw new IllegalArgumentException("The distance should be a positive number");
  }

  public ScrollDirection getDirection() {
    return this.scrollDirection;
  }

  public int getDistance() {
    return this.distance;
  }
}
