package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;

@ParametersAreNonnullByDefault
public class ClickOptions {
  private final int offsetX;
  private final int offsetY;
  private final ClickMethod clickMethod;

  private ClickOptions(ClickMethod clickMethod, int offsetX, int offsetY) {
    this.clickMethod = clickMethod;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  @CheckReturnValue
  @Nonnull
  public static ClickOptions usingDefaultMethod() {
    return new ClickOptions(DEFAULT, 0, 0);
  }

  @CheckReturnValue
  @Nonnull
  public static ClickOptions usingJavaScript() {
    return new ClickOptions(JS, 0, 0);
  }

  @CheckReturnValue
  public int offsetX() {
    return offsetX;
  }

  @CheckReturnValue
  public int offsetY() {
    return offsetY;
  }

  @CheckReturnValue
  @Nonnull
  public ClickMethod clickOption() {
    return clickMethod;
  }

  @CheckReturnValue
  @Nonnull
  public ClickOptions offsetX(int offsetX) {
    return new ClickOptions(clickMethod, offsetX, offsetY);
  }

  @CheckReturnValue
  @Nonnull
  public ClickOptions offsetY(int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY);
  }

  @CheckReturnValue
  @Nonnull
  public ClickOptions offset(int offsetX, int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY);
  }

  @Override
  public String toString() {
    if (offsetX == 0 && offsetY == 0)
      return String.format("method: %s", clickMethod);
    else
      return String.format("method: %s, offsetX: %s, offsetY: %s", clickMethod, offsetX, offsetY);
  }
}
