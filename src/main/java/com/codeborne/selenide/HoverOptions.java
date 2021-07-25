package com.codeborne.selenide;

public class HoverOptions {
  private final int offsetX;
  private final int offsetY;

  private HoverOptions(int offsetX, int offsetY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public static HoverOptions withOffset(int offsetX, int offsetY) {
    return new HoverOptions(offsetX, offsetY);
  }

  public int offsetX() {
    return offsetX;
  }

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
