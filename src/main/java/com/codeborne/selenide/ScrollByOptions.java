package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ScrollByDirection.DOWN;

@ParametersAreNonnullByDefault
public class ScrollByOptions {
  private final ScrollByDirection scrollByDirection;
  private final int distance;
  private static final ScrollByDirection DEFAULT_SCROLL_DIRECTION = DOWN;
  private static final int DEFAULT_SCROLL_LENGHT = 1000;

  @CheckReturnValue
  @Nonnull
  protected ScrollByOptions(ScrollByDirection scrollByDirection, int distance) {
    this.scrollByDirection = scrollByDirection;
    this.distance = distance;
  }

  @CheckReturnValue
  @Nonnull
  public static ScrollByOptions defaultScrollByOptions() {
    return new ScrollByOptions(DEFAULT_SCROLL_DIRECTION, DEFAULT_SCROLL_LENGHT);
  }

  @CheckReturnValue
  @Nonnull
  public static ScrollByOptions direction(ScrollByDirection scrollByDirection) {
    return new ScrollByOptions(scrollByDirection, DEFAULT_SCROLL_LENGHT);
  }

  @CheckReturnValue
  @Nonnull
  public ScrollByOptions distance(int distance) {
    if (distance >= 0)
      return new ScrollByOptions(scrollByDirection, distance);
    else
      throw new IllegalArgumentException("The distance should be a positive number");
  }

  @CheckReturnValue
  @Nonnull
  public ScrollByDirection getDirection() {
    return this.scrollByDirection;
  }

  @CheckReturnValue
  @Nonnull
  public int getDistance() {
    return this.distance;
  }
}
