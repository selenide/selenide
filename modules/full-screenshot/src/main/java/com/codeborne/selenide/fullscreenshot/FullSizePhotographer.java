package com.codeborne.selenide.fullscreenshot;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.Photographer;
import com.codeborne.selenide.impl.WebdriverPhotographer;
import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v116.page.Page;
import org.openqa.selenium.devtools.v116.page.model.Viewport;
import org.openqa.selenium.firefox.HasFullPageScreenshot;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link Photographer} which can take full-size screenshots.
 *
 * @since 6.7.0
 */
@ParametersAreNonnullByDefault
public class FullSizePhotographer implements Photographer {
  private static final Logger log = LoggerFactory.getLogger(FullSizePhotographer.class);
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
    try {
      Optional<T> result = takeFullSizeScreenshot(driver, outputType);
      return result.isPresent() ? result :
        defaultImplementation.takeScreenshot(driver, outputType);
    }
    catch (WebDriverException e) {
      log.error("Failed to take full-size screenshot", e);
      return defaultImplementation.takeScreenshot(driver, outputType);
    }
  }

  @Nonnull
  private <T> Optional<T> takeFullSizeScreenshot(Driver driver, OutputType<T> outputType) {
    WebDriver webDriver = unwrap(driver);

    if (webDriver instanceof HasFullPageScreenshot firefoxDriver) {
      return Optional.of(firefoxDriver.getFullPageScreenshotAs(outputType));
    }
    if (webDriver instanceof HasCdp) {
      return takeScreenshotWithCDP((WebDriver & HasCdp) webDriver, outputType);
    }
    if (webDriver instanceof HasDevTools) {
      return takeScreenshot((WebDriver & HasDevTools) webDriver, outputType);
    }
    return Optional.empty();
  }

  private WebDriver unwrap(Driver driver) {
    WebDriver webDriver = driver.getWebDriver();
    if (webDriver instanceof WrapsDriver wrapper) {
      webDriver = wrapper.getWrappedDriver();
    }
    if (webDriver instanceof RemoteWebDriver remoteWebDriver) {
      webDriver = new Augmenter().augment(remoteWebDriver);
    }
    return webDriver;
  }

  @Nonnull
  @CheckReturnValue
  private <WD extends WebDriver & HasDevTools, ResultType> Optional<ResultType> takeScreenshot(
    WD devtoolsDriver, OutputType<ResultType> outputType
  ) {
    DevTools devTools = devtoolsDriver.getDevTools();
    devTools.createSessionIfThereIsNotOne(devtoolsDriver.getWindowHandle());

    Options options = getOptions(devtoolsDriver);
    Viewport viewport = new Viewport(0, 0, options.fullWidth(), options.fullHeight(), 1);

    String base64 = devTools.send(Page.captureScreenshot(
        Optional.empty(),
        Optional.empty(),
        Optional.of(viewport),
        Optional.empty(),
        Optional.of(options.exceedViewport()),
        Optional.of(true)
      )
    );

    ResultType screenshot = outputType.convertFromBase64Png(base64);
    return Optional.of(screenshot);
  }

  @Nonnull
  @CheckReturnValue
  private <WD extends WebDriver & HasCdp, ResultType> Optional<ResultType> takeScreenshotWithCDP(
    WD cdpDriver, OutputType<ResultType> outputType
  ) {
    Options options = getOptions(cdpDriver);
    Map<String, Object> captureScreenshotOptions = ImmutableMap.of(
      "clip", ImmutableMap.of(
        "x", 0,
        "y", 0,
        "width", options.fullWidth(),
        "height", options.fullHeight(),
        "scale", 1),
      "captureBeyondViewport", options.exceedViewport()
    );

    Map<String, Object> result = cdpDriver.executeCdpCommand("Page.captureScreenshot", captureScreenshotOptions);

    String base64 = (String) result.get("data");
    ResultType screenshot = outputType.convertFromBase64Png(base64);
    return Optional.of(screenshot);
  }

  private Options getOptions(WebDriver webDriver) {
    Map<String, Object> size = js.execute(webDriver);
    return new Options((long) size.get("fullWidth"), (long) size.get("fullHeight"), (boolean) size.get("exceedViewport"));
  }

  @Desugar
  private record Options(long fullWidth, long fullHeight, boolean exceedViewport) {
  }
}
