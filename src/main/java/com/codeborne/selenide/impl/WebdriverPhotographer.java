package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v101.page.Page;
import org.openqa.selenium.devtools.v101.emulation.Emulation;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

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
    if (driver.getWebDriver() instanceof ChromiumDriver chromiumDriver) {
      long width = (long) chromiumDriver.executeScript("return document.body.scrollWidth");
      long height = (long) chromiumDriver.executeScript("return document.body.scrollHeight");
      long scale = (long) chromiumDriver.executeScript("return window.devicePixelRatio");

      HashMap<String, Object> setDeviceMetricsOverride = new HashMap<>();
      setDeviceMetricsOverride.put("mobile", false);
      setDeviceMetricsOverride.put("width", width);
      setDeviceMetricsOverride.put("height", height);
      setDeviceMetricsOverride.put("deviceScaleFactor", scale);
      chromiumDriver.executeCdpCommand("Emulation.setDeviceMetricsOverride", setDeviceMetricsOverride);

      Map<String, Object> result = chromiumDriver.executeCdpCommand("Page.captureScreenshot", new HashMap<>());

      chromiumDriver.executeCdpCommand("Emulation.clearDeviceMetricsOverride", new HashMap<>());

      String base64 = (String) result.get("data");
      T screenshot = outputType.convertFromBase64Png(base64);
      return Optional.of(screenshot);
    }

    if (driver.getWebDriver() instanceof RemoteWebDriver remoteWebDriver) {
      WebDriver webDriver = new Augmenter().augment(remoteWebDriver);
      DevTools devTools = ((HasDevTools) webDriver).getDevTools();
      devTools.createSession();

      Integer width = (int) (long) remoteWebDriver.executeScript("return document.body.scrollWidth");
      Integer height = (int) (long) remoteWebDriver.executeScript("return document.body.scrollHeight");
      long scale = (long) remoteWebDriver.executeScript("return window.devicePixelRatio");

      devTools.send(Emulation.setDeviceMetricsOverride(
          width,
          height,
          scale,
          false,
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty()
        )
      );

      String base64 = devTools.send(Page.captureScreenshot(
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
        Optional.empty()
        )
      );

      devTools.send(Emulation.clearDeviceMetricsOverride());

      T screenshot = outputType.convertFromBase64Png(base64);
      return Optional.of(screenshot);
    }

    return Optional.empty();
  }
}
