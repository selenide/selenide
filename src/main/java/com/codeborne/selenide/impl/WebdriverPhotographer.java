package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.github.bsideup.jabel.Desugar;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v101.page.Page;
import org.openqa.selenium.devtools.v101.page.model.Viewport;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.EDGE;

@ParametersAreNonnullByDefault
public class WebdriverPhotographer implements Photographer {
  private static final JavaScript js = new JavaScript("get-screen-size.js");

  @Nonnull
  @CheckReturnValue
  @Override
  public <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType) {
    if (driver.getWebDriver() instanceof ChromiumDriver chromiumDriver) {
      Options options = getOptions(chromiumDriver);
      HashMap<String, Object> captureScreenshotOptions = new HashMap<>() {{
        put("clip", new HashMap<>() {{
          put("x", 0);
          put("y", 0);
          put("width", options.fullWidth());
          put("height", options.fullHeight());
          put("scale", 1);
        }});
        put("captureBeyondViewport", options.exceedViewport());
      }};

      Map<String, Object> result = chromiumDriver.executeCdpCommand("Page.captureScreenshot", captureScreenshotOptions);

      String base64 = (String) result.get("data");
      T screenshot = outputType.convertFromBase64Png(base64);
      return Optional.of(screenshot);
    }

    if (driver.getWebDriver() instanceof RemoteWebDriver remoteWebDriver) {
      String browserName = remoteWebDriver.getCapabilities().getBrowserName();
      if (!(browserName.equalsIgnoreCase(CHROME) || browserName.equalsIgnoreCase(EDGE))) {
        return takeDefaultScreenshot(driver, outputType);
      }

      WebDriver webDriver = new Augmenter().augment(remoteWebDriver);
      DevTools devTools = ((HasDevTools) webDriver).getDevTools();
      devTools.createSession();

      Options options = getOptions(remoteWebDriver);
      Viewport viewport = new Viewport(0, 0, options.fullWidth(), options.fullHeight(), 1);

      String base64 = devTools.send(Page.captureScreenshot(
          Optional.empty(),
          Optional.empty(),
          Optional.of(viewport),
          Optional.empty(),
          Optional.of(options.exceedViewport())
        )
      );

      T screenshot = outputType.convertFromBase64Png(base64);
      return Optional.of(screenshot);
    }

    return takeDefaultScreenshot(driver, outputType);
  }

  private <T> Optional<T> takeDefaultScreenshot(Driver driver, OutputType<T> outputType) {
    if (driver.getWebDriver() instanceof TakesScreenshot) {
      T screenshot = ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(outputType);
      return Optional.of(screenshot);
    }
    return Optional.empty();
  }

  private Options getOptions(RemoteWebDriver webDriver) {
    Map<String, Object> size = js.execute(webDriver);
    return new Options((long) size.get("fullWidth"), (long) size.get("fullHeight"), (boolean) size.get("exceedViewport"));
  }

  @Desugar
  private record Options(long fullWidth, long fullHeight, boolean exceedViewport) {
  }
}
