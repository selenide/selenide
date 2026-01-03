package com.codeborne.selenide;

public record HoverOptions(
  int offsetX,
  int offsetY
) {
  public static HoverOptions withOffset(int offsetX, int offsetY) {
    return new HoverOptions(offsetX, offsetY);
  }

  @Override
  public String toString() {
    if (offsetX == 0 && offsetY == 0)
      return "";
    else
      return String.format("offsetX: %s, offsetY: %s", offsetX, offsetY);
  }
}
