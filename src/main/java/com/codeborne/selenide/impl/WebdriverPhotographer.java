package com.codeborne.selenide.impl;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v144.page.Page;
import org.openqa.selenium.remote.http.ConnectionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;

import static com.codeborne.selenide.impl.BiDiUti.isBiDiEnabled;

public class WebdriverPhotographer implements Photographer {
  private static final Logger log = LoggerFactory.getLogger(WebdriverPhotographer.class);

  @Override
  public <T> Optional<T> takeScreenshot(WebDriver webDriver, OutputType<T> outputType) {
    if (isDevToolsEnabled(webDriver)) { // Chromium - HasDevTools is the fastest way
      return Optional.of(outputType.convertFromBase64Png(takeScreenshotWithDevtools((WebDriver & HasDevTools) webDriver)));
    }
    else if (isBiDiEnabled(webDriver)) { // Firefox - BiDi is the fastest
      return Optional.of(outputType.convertFromBase64Png(takeScreenshotWithBidi((WebDriver & HasBiDi) webDriver)));
    }
    else if (webDriver instanceof TakesScreenshot takesScreenshot) { // other browsers
      T screenshot = takesScreenshot.getScreenshotAs(outputType);
      return Optional.of(screenshot);
    }
    return Optional.empty();
  }

  private static boolean isDevToolsEnabled(WebDriver webDriver) {
    if (!(webDriver instanceof HasDevTools hasDevTools)) {
      return false;
    }
    try {
      hasDevTools.getDevTools();
      return true;
    }
    catch (ConnectionFailedException notEnabled) {
      log.warn("Failed to establish DevTools connection: {}", notEnabled.toString());
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
