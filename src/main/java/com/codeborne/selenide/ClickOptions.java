package com.codeborne.selenide;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;

public class ClickOptions {

  private int offsetX;
  private int offsetY;
  private final ClickMethod clickMethod;

  private ClickOptions(ClickMethod clickMethod) {
    this.clickMethod = clickMethod;
  }

  public static ClickOptions usingDefaultMethod() {
    return new ClickOptions(DEFAULT);
  }

  public static ClickOptions usingJavaScript() {
    return new ClickOptions(JS);
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
    this.offsetX = offsetX;
    return this;
  }

  public ClickOptions offsetY(int offsetY) {
    this.offsetY = offsetY;
    return this;
  }

  public ClickOptions offset(int offsetX, int offsetY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    return this;
  }
}
