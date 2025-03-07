package com.codeborne.selenide.appium.selector;

import com.codeborne.selenide.WebDriverRunner;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static java.util.Objects.requireNonNull;

public class CombinedBy extends By {
  @Nullable
  private final By androidSelector;

  @Nullable
  private final By iosSelector;

  private CombinedBy(@Nullable By androidSelector, @Nullable By iosSelector) {
    this.androidSelector = androidSelector;
    this.iosSelector = iosSelector;
  }

  public static CombinedBy android(By android) {
    return new CombinedBy(android, null);
  }

  public CombinedBy ios(By ios) {
    return new CombinedBy(androidSelector, ios);
  }

  @Override
  public WebElement findElement(SearchContext context) {
    return chooseSelector(context).findElement(context);
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    return chooseSelector(context).findElements(context);
  }

  private By chooseSelector(SearchContext context) {
    if (isAndroid(context)) {
      return requireNonNull(androidSelector, "Android selector not given");
    }
    if (isIos(context)) {
      return requireNonNull(iosSelector, "iOS selector not given");
    }
    throw new UnsupportedOperationException("Unsupported webdriver: " + WebDriverRunner.getWebDriver());
  }

  @Override
  public String toString() {
    if (androidSelector != null && iosSelector != null) {
      return String.format("[android:%s, ios:%s]", androidSelector, iosSelector);
    }
    if (androidSelector != null) {
      return androidSelector.toString();
    }
    if (iosSelector != null) {
      return iosSelector.toString();
    }
    return "CombinedBy: null";
  }
}
