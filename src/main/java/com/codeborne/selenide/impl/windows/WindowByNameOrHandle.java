package com.codeborne.selenide.impl.windows;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public class WindowByNameOrHandle implements ExpectedCondition<WebDriver> {
  private final String nameOrHandleOrTitle;

  public WindowByNameOrHandle(String nameOrHandleOrTitle) {
    this.nameOrHandleOrTitle = nameOrHandleOrTitle;
  }

  @Override
  @Nullable
  public WebDriver apply(WebDriver driver) {
    try {
      return requireNonNull(driver).switchTo().window(nameOrHandleOrTitle);
    } catch (NoSuchWindowException windowWithNameOrHandleNotFound) {
      try {
        return windowByTitle(driver, nameOrHandleOrTitle);
      } catch (NoSuchWindowException e) {
        //noinspection DataFlowIssue
        return null;
      }
    }
  }

  @Override
  public String toString() {
    return "window to be available by name or handle or title: " + nameOrHandleOrTitle;
  }

  /**
   * Switch to window/tab by name/handle/title except some windows handles
   * @param title title of window/tab
   */
  private WebDriver windowByTitle(WebDriver driver, String title) {
    Set<String> windowHandles = driver.getWindowHandles();

    for (String windowHandle : windowHandles) {
      driver.switchTo().window(windowHandle);
      if (title.equals(driver.getTitle())) {
        return driver;
      }
    }
    throw new NoSuchWindowException("Window with title not found: " + title);
  }
}
