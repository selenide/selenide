package com.codeborne.selenide.appium.selector;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;
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
    return chooseSelector(context).findElement(context);
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    return chooseSelector(context).findElements(context);
  }

  private By chooseSelector(SearchContext context) {
    if (instanceOf(context, AndroidDriver.class)) {
      return requireNonNull(androidSelector, "Android selector not given");
    }
    if (instanceOf(context, IOSDriver.class)) {
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
