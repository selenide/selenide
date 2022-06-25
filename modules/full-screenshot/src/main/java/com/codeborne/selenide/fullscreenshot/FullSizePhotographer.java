package com.codeborne.selenide.fullscreenshot;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.Photographer;
import com.codeborne.selenide.impl.WebdriverPhotographer;
import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v101.page.Page;
import org.openqa.selenium.devtools.v101.page.model.Viewport;
import org.openqa.selenium.firefox.HasFullPageScreenshot;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link Photographer} which can take full-size screenshots.
 * @since 6.7.0
 */
@ParametersAreNonnullByDefault
public class FullSizePhotographer implements Photographer {
  private static final JavaScript js = new JavaScript("get-screen-size.js");

  private final WebdriverPhotographer defaultImplementation;

  public FullSizePhotographer() {
    this(new WebdriverPhotographer());
  }

  protected FullSizePhotographer(WebdriverPhotographer defaultImplementation) {
    this.defaultImplementation = defaultImplementation;
  }

  @Nonnull
  @Override
  @CheckReturnValue
  public <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType) {
    WebDriver wd = driver.getWebDriver();
    if (wd instanceof WrapsDriver) {
      wd = ((WrapsDriver) wd).getWrappedDriver();
    }
    if (wd instanceof HasFullPageScreenshot webDriver) {
      return Optional.of(webDriver.getFullPageScreenshotAs(outputType));
    }
    if (wd instanceof HasCdp cdp) {
      Options options = getOptions(wd);
      Map<String, Object> captureScreenshotOptions = ImmutableMap.of(
        "clip", ImmutableMap.of(
          "x", 0,
          "y", 0,
          "width", options.fullWidth(),
          "height", options.fullHeight(),
          "scale", 1),
        "captureBeyondViewport", options.exceedViewport()
      );

      Map<String, Object> result = cdp.executeCdpCommand("Page.captureScreenshot", captureScreenshotOptions);

      String base64 = (String) result.get("data");
      T screenshot = outputType.convertFromBase64Png(base64);
      return Optional.of(screenshot);
    }

    if (wd instanceof RemoteWebDriver remoteWebDriver) {
      WebDriver webDriver = new Augmenter().augment(remoteWebDriver);
      if (webDriver instanceof HasFullPageScreenshot smartWebDriver) {
        return Optional.of(smartWebDriver.getFullPageScreenshotAs(outputType));
      }
      if (!(webDriver instanceof HasDevTools)) {
        return defaultImplementation.takeScreenshot(driver, outputType);
      }
      DevTools devTools = ((HasDevTools) webDriver).getDevTools();
      devTools.createSessionIfThereIsNotOne();

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
    return Optional.empty();
  }

  private Options getOptions(WebDriver webDriver) {
    Map<String, Object> size = js.execute(webDriver);
    return new Options((long) size.get("fullWidth"), (long) size.get("fullHeight"), (boolean) size.get("exceedViewport"));
  }

  @Desugar
  private record Options(long fullWidth, long fullHeight, boolean exceedViewport) {
  }
}
