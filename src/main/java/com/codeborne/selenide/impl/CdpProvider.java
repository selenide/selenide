package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.openqa.selenium.devtools.DevToolsException;

@ParametersAreNonnullByDefault
public interface CdpProvider {
  @Nonnull
  @CheckReturnValue
  DevTools getCdp(Driver driver);

  default void chromiumGuard(Driver driver) {
    HasCapabilities hasCapabilities = (HasCapabilities) driver.getWebDriver();
    boolean isChromium = ChromiumDriver.IS_CHROMIUM_BROWSER
      .test(hasCapabilities.getCapabilities().getBrowserName());
    if (!isChromium) {
      throw new DevToolsException("Cannot create devtools for non-chromium browser");
    }
  }
}
