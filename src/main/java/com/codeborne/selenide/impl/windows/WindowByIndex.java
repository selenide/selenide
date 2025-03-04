package com.codeborne.selenide.impl.windows;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.ArrayList;
import java.util.List;

public class WindowByIndex implements ExpectedCondition<WebDriver> {
  private final int index;

  public WindowByIndex(int index) {
    this.index = index;
  }

  @Override
  @Nullable
  public WebDriver apply(WebDriver driver) {
    try {
      List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
      return driver.switchTo().window(windowHandles.get(index));
    } catch (IndexOutOfBoundsException windowWithIndexNotFound) {
      //noinspection DataFlowIssue
      return null;
    }
  }

  @Override
  public String toString() {
    return "window to be available by index: " + index;
  }
}
