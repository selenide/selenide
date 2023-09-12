package com.codeborne.selenide.appium.selector;

import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class ByClassNameAndIndex extends By.ByXPath {

  protected final String className;
  protected final int index;

  public ByClassNameAndIndex(String className, int index) {
    super(String.format("(.//%s)[%s]", className, index));
    this.className = className;
    this.index = index;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return String.format("(.//%s)[%s]", className, index);
  }
}
