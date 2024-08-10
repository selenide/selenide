package com.codeborne.selenide.appium.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isAndroid;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isIos;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class CombinedAttribute {
  @Nullable
  private final String androidAttribute;

  @Nullable
  private final String iosAttribute;

  private CombinedAttribute(@Nullable String androidAttribute, @Nullable String iosAttribute) {
    this.androidAttribute = androidAttribute;
    this.iosAttribute = iosAttribute;
  }

  @Nonnull
  @CheckReturnValue
  public static CombinedAttribute android(String android) {
    return new CombinedAttribute(android, null);
  }

  @Nonnull
  @CheckReturnValue
  public CombinedAttribute ios(String ios) {
    return new CombinedAttribute(androidAttribute, ios);
  }

  @Nullable
  @CheckReturnValue
  public String getAttributeValue(Driver driver, WebElement element) {
    return element.getAttribute(getAttributeName(driver));
  }

  @Nonnull
  @CheckReturnValue
  private String getAttributeName(Driver driver) {
    if (isAndroid(driver)) {
      return requireNonNull(androidAttribute, "Android selector not given");
    }
    if (isIos(driver)) {
      return requireNonNull(iosAttribute, "iOS selector not given");
    }
    throw new UnsupportedOperationException("Unsupported webdriver: " + WebDriverRunner.getWebDriver());
  }
}
