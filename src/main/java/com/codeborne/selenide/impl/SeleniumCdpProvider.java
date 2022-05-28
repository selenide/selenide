package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.Augmenter;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SeleniumCdpProvider implements CdpProvider {
  @Nonnull
  @CheckReturnValue
  public DevTools getCdp(Driver driver) {
    chromiumGuard(driver);
    WebDriver webDriver = new Augmenter().augment(driver.getWebDriver());
    DevTools devTools = ((HasDevTools) webDriver).getDevTools();
    devTools.createSessionIfThereIsNotOne();
    return devTools;
  }
}
