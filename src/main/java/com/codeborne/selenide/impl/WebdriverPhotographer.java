package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class WebdriverPhotographer implements Photographer {
  @Nonnull
  @CheckReturnValue
  @Override
  public <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType) {
    if (driver.config().fullPageScreenshots()) {
      return takeFullScreenshot(driver, outputType);
    } else {
      return takeVisibleScreenshot(driver, outputType);
    }
  }

  public <T> Optional<T> takeVisibleScreenshot(Driver driver, OutputType<T> outputType) {
    if (driver.getWebDriver() instanceof TakesScreenshot) {
      T screenshot = ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(outputType);
      return Optional.of(screenshot);
    }
    return Optional.empty();
  }

  public <T> Optional<T> takeFullScreenshot(Driver driver, OutputType<T> outputType) {
    if (driver.getWebDriver() instanceof ChromeDriver chromeDriver) {
      long width = (long) chromeDriver.executeScript("return document.body.scrollWidth");
      long height = (long) chromeDriver.executeScript("return document.body.scrollHeight");
      long scale = (long) chromeDriver.executeScript("return window.devicePixelRatio");

      HashMap<String, Object> setDeviceMetricsOverride = new HashMap<>();
      setDeviceMetricsOverride.put("mobile", false);
      setDeviceMetricsOverride.put("width", width);
      setDeviceMetricsOverride.put("height", height);
      setDeviceMetricsOverride.put("deviceScaleFactor", scale);
      chromeDriver.executeCdpCommand("Emulation.setDeviceMetricsOverride", setDeviceMetricsOverride);

      Map<String, Object> result = chromeDriver.executeCdpCommand("Page.captureScreenshot", new HashMap<>());

      chromeDriver.executeCdpCommand("Emulation.clearDeviceMetricsOverride", new HashMap<>());

      String base64 = (String) result.get("data");
      T screenshot = outputType.convertFromBase64Png(base64);
      return Optional.of(screenshot);
    }
    return Optional.empty();
  }
}
