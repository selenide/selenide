package com.codeborne.selenide.appium.selector;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.appium.AppiumDriverRunner.isAndroidDriver;
import static com.codeborne.selenide.appium.AppiumDriverRunner.isIosDriver;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
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
    return chooseSelector().findElement(context);
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    return chooseSelector().findElements(context);
  }

  private By chooseSelector() {
    if (isAndroidDriver()) {
      return requireNonNull(androidSelector, "Android selector not given");
    }
    if (isIosDriver()) {
      return requireNonNull(iosSelector, "iOS selector not given");
    }
    throw new UnsupportedOperationException("Unsupported webdriver: " + WebDriverRunner.getWebDriver());
  }
}
