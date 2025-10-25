package com.codeborne.selenide.impl;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v142.page.Page;

import java.time.Duration;
import java.util.Optional;

public class WebdriverPhotographer implements Photographer {
  @Override
  public <T> Optional<T> takeScreenshot(WebDriver webDriver, OutputType<T> outputType) {
    if (webDriver instanceof HasDevTools hasDevTools) { // Chromium - HasDevTools is the fastest way
      return Optional.of(outputType.convertFromBase64Png(takeScreenshotWithDevtools((WebDriver & HasDevTools) hasDevTools)));
    }
    else if (webDriver instanceof HasBiDi hasBiDi && isBiDiEnabled(hasBiDi)) { // Firefox - BiDi is the fastest
      return Optional.of(outputType.convertFromBase64Png(takeScreenshotWithBidi((WebDriver & HasBiDi) hasBiDi)));
    }
    else if (webDriver instanceof TakesScreenshot takesScreenshot) { // other browsers
      T screenshot = takesScreenshot.getScreenshotAs(outputType);
      return Optional.of(screenshot);
    }
    return Optional.empty();
  }

  private boolean isBiDiEnabled(HasBiDi hasBiDi) {
    try {
      hasBiDi.getBiDi();
      return true;
    }
    catch (BiDiException notEnabled) {
      return false;
    }
  }

  private <T extends WebDriver & HasDevTools> String takeScreenshotWithDevtools(T driver) {
    String windowHandle = driver.getWindowHandle();
    DevTools devTools = driver.getDevTools();
    devTools.createSessionIfThereIsNotOne(windowHandle);

    return devTools.send(Page.captureScreenshot(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(true)
      ), Duration.ofSeconds(4)
    );
  }

  private <T extends WebDriver & HasBiDi> String takeScreenshotWithBidi(T webDriver) {
    String windowHandle = webDriver.getWindowHandle();
    BrowsingContext browsingContext = new BrowsingContext(webDriver, windowHandle);
    return browsingContext.captureScreenshot();
  }
}
