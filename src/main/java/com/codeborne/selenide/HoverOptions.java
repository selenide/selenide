package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HoverOptions {
  private final int offsetX;
  private final int offsetY;

  private HoverOptions(int offsetX, int offsetY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  @CheckReturnValue
  @Nonnull
  public static HoverOptions withOffset(int offsetX, int offsetY) {
    return new HoverOptions(offsetX, offsetY);
  }

  @CheckReturnValue
  public int offsetX() {
    return offsetX;
  }

  @CheckReturnValue
  public int offsetY() {
    return offsetY;
  }

  @Override
  public String toString() {
    if (offsetX == 0 && offsetY == 0)
      return "";
    else
      return String.format("offsetX: %s, offsetY: %s", offsetX, offsetY);
  }
}
