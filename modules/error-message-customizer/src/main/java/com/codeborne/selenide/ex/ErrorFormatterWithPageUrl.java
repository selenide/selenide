package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebDriverException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ErrorFormatterWithPageUrl extends SelenideErrorFormatter {
  @Nonnull
  @Override
  public String uiDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs) {
    return super.uiDetails(error, driver, screenshot, timeoutMs) + pageUrl(driver);
  }

  @CheckReturnValue
  @Nonnull
  protected String pageUrl(Driver driver) {
    String pageUrl = getPageUrl(driver);
    return pageUrl == null ? "" : String.format("%nPage url: %s", pageUrl);
  }

  @CheckReturnValue
  @Nullable
  private String getPageUrl(Driver driver) {
    try {
      return driver.url();
    }
    catch (WebDriverException commandNotImplemented) {
      return null;
    }
  }
}
