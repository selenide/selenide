package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public interface Photographer {
  <T> Optional<T> takeScreenshot(WebDriver webDriver, OutputType<T> outputType);

  /**
   * @deprecated Override method {@link #takeScreenshot(WebDriver, OutputType)} instead
   */
  @Deprecated(forRemoval = true)
  default <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType) {
    return takeScreenshot(driver.getWebDriver(), outputType);
  }
}
