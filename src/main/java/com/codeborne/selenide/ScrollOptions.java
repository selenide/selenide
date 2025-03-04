package com.codeborne.selenide;

import static com.codeborne.selenide.ScrollDirection.DOWN;

public record ScrollOptions(
  ScrollDirection direction,
  int distance
) {
  private static final ScrollDirection DEFAULT_SCROLL_DIRECTION = DOWN;
  private static final int DEFAULT_SCROLL_DISTANCE_IN_PIXELS = 1000;

  public static ScrollOptions defaultScrollOptions() {
    return new ScrollOptions(DEFAULT_SCROLL_DIRECTION, DEFAULT_SCROLL_DISTANCE_IN_PIXELS);
  }

  public static ScrollOptions direction(ScrollDirection scrollDirection) {
    return new ScrollOptions(scrollDirection, DEFAULT_SCROLL_DISTANCE_IN_PIXELS);
  }

  public ScrollOptions distance(int distance) {
    if (distance >= 0)
      return new ScrollOptions(direction, distance);
    else
      throw new IllegalArgumentException("The distance should be a positive number");
  }
}
