package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;

public class WebElementUtils {
  public static WebElement unwrap(WebElement element) {
    WebElement result = element;
    for (int i = 0; result instanceof WrapsElement && i < 42; i++) {
      result = ((WrapsElement) result).getWrappedElement();
    }
    return result;
  }
}
