package com.codeborne.selenide;

import static com.codeborne.selenide.ClickOption.*;

public class ClickParams {

  private int offsetX;
  private int offsetY;
  private final ClickOption clickOption;

  private ClickParams(ClickOption clickOption) {
    this.clickOption = clickOption;
  }

  public static ClickParams usingJavaScript() {
    return new ClickParams(JS);
  }

  public int offsetX() {
    return offsetX;
  }

  public int offsetY() {
    return offsetY;
  }

  public ClickOption clickOption() {
    return clickOption;
  }

  public ClickParams offsetX(int offsetX) {
    this.offsetX = offsetX;
    return this;
  }

  public ClickParams offsetY(int offsetY) {
    this.offsetY = offsetY;
    return this;
  }

  public ClickParams offset(int offsetX, int offsetY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    return this;
  }
}
