package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

import static java.util.Collections.singletonList;

public class ShadowRoot extends By implements Serializable {
  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> findElements(SearchContext context) {
    if (context instanceof WrapsDriver) {
      WebDriver driver = ((WrapsDriver) context).getWrappedDriver();
      WebElement shadowRoot = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", context);
      return singletonList(shadowRoot);
    }
    else {
      throw new IllegalArgumentException("Cannot find shadow root of " + context);
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "#shadow-root";
  }
}
