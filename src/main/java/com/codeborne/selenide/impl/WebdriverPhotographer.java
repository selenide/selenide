package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.Optional;

public class WebdriverPhotographer implements Photographer {
  @Override
  public <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType) {
    if (driver.getWebDriver() instanceof TakesScreenshot takesScreenshot) {
      T screenshot = takesScreenshot.getScreenshotAs(outputType);
      return Optional.of(screenshot);
    }
    return Optional.empty();
  }
}
