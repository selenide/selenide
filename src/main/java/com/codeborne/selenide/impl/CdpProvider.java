package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
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

  default void requireChromium(WebDriver driver) {
    if (driver instanceof HasCapabilities hasCapabilities) {
      boolean isChromium = new Browser(hasCapabilities.getCapabilities().getBrowserName(), false).isChromium();
      if (!isChromium) {
        throw new DevToolsException("Cannot create devtools for non-chromium browser");
      }
    } else {
      throw new IllegalArgumentException("Driver must have capabilities");
    }
  }
}
