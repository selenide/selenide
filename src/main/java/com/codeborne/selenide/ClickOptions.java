package com.codeborne.selenide;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;

public class ClickOptions {
  private final int offsetX;
  private final int offsetY;
  private final ClickMethod clickMethod;

  private ClickOptions(ClickMethod clickMethod, int offsetX, int offsetY) {
    this.clickMethod = clickMethod;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public static ClickOptions usingDefaultMethod() {
    return new ClickOptions(DEFAULT, 0, 0);
  }

  public static ClickOptions usingJavaScript() {
    return new ClickOptions(JS, 0, 0);
  }

  public int offsetX() {
    return offsetX;
  }

  public int offsetY() {
    return offsetY;
  }

  public ClickMethod clickOption() {
    return clickMethod;
  }

  public ClickOptions offsetX(int offsetX) {
    return new ClickOptions(clickMethod, offsetX, offsetY);
  }

  public ClickOptions offsetY(int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY);
  }

  public ClickOptions offset(int offsetX, int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY);
  }
}
