package com.codeborne.selenide.impl.windows;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WindowByIndex implements ExpectedCondition<WebDriver> {
  private final int index;

  public WindowByIndex(int index) {
    this.index = index;
  }

  @Override
  @Nullable
  public WebDriver apply(@SuppressWarnings("NullableProblems") WebDriver driver) {
    try {
      List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
      return driver.switchTo().window(windowHandles.get(index));
    } catch (IndexOutOfBoundsException windowWithIndexNotFound) {
      return null;
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "window to be available by index: " + index;
  }
}
