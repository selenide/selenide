package com.codeborne.selenide.fullscreenshot;

import com.codeborne.selenide.impl.Photographer;
import com.codeborne.selenide.impl.WebdriverPhotographer;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.latest.dom.model.Rect;
import org.openqa.selenium.devtools.latest.page.Page;
import org.openqa.selenium.devtools.latest.page.model.VisualViewport;
import org.openqa.selenium.devtools.latest.page.model.Viewport;
import org.openqa.selenium.firefox.HasFullPageScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link Photographer} which can take full-size screenshots.
 */
public class FullSizePhotographer implements Photographer {
  private static final Logger log = LoggerFactory.getLogger(FullSizePhotographer.class);

  private final WebdriverPhotographer defaultImplementation;

  public FullSizePhotographer() {
    this(new WebdriverPhotographer());
  }

  protected FullSizePhotographer(WebdriverPhotographer defaultImplementation) {
    this.defaultImplementation = defaultImplementation;
  }

  @Override
  public <T> Optional<T> takeScreenshot(WebDriver webDriver, OutputType<T> outputType) {
    try {
      Optional<T> result = takeFullSizeScreenshot(webDriver, outputType);
      return result.isPresent() ? result :
        defaultImplementation.takeScreenshot(webDriver, outputType);
    }
    catch (WebDriverException e) {
      log.error("Failed to take full-size screenshot", e);
      return defaultImplementation.takeScreenshot(webDriver, outputType);
    }
  }

  private <T> Optional<T> takeFullSizeScreenshot(WebDriver webDriver, OutputType<T> outputType) {
    if (webDriver instanceof HasFullPageScreenshot firefoxDriver) {
      return Optional.of(firefoxDriver.getFullPageScreenshotAs(outputType));
    }
    if (webDriver instanceof HasCdp) {
      return takeScreenshotWithCDP((WebDriver & HasCdp) webDriver, outputType);
    }
    if (webDriver instanceof HasDevTools) {
      return takeScreenshotWithDevTools((WebDriver & HasDevTools) webDriver, outputType);
    }
    return Optional.empty();
  }

  private <WD extends WebDriver & HasDevTools, ResultType> Optional<ResultType> takeScreenshotWithDevTools(
    WD devtoolsDriver, OutputType<ResultType> outputType
  ) {
    DevTools devTools = devtoolsDriver.getDevTools();
    devTools.createSessionIfThereIsNotOne(devtoolsDriver.getWindowHandle());

    Options options = getOptions(devTools);
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

  private <WD extends WebDriver & HasCdp, ResultType> Optional<ResultType> takeScreenshotWithCDP(
    WD cdpDriver, OutputType<ResultType> outputType
  ) {
    Options options = getOptions(cdpDriver);
    Map<String, Object> captureScreenshotOptions = Map.of(
      "clip", Map.of(
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

  private Options getOptions(DevTools devTools) {
    Page.GetLayoutMetricsResponse metrics = devTools.send(Page.getLayoutMetrics());
    Rect contentSize = metrics.getCssContentSize();
    VisualViewport viewport = metrics.getCssVisualViewport();
    return toOptions(contentSize.getWidth(), contentSize.getHeight(), viewport.getClientWidth(), viewport.getClientHeight());
  }

  private Options getOptions(HasCdp cdpDriver) {
    Map<String, Object> metrics = cdpDriver.executeCdpCommand("Page.getLayoutMetrics", Map.of());
    Map<?, ?> contentSize = (Map<?, ?>) metrics.get("cssContentSize");
    Map<?, ?> viewport = (Map<?, ?>) metrics.get("cssVisualViewport");
    return toOptions(
      (Number) contentSize.get("width"), (Number) contentSize.get("height"),
      (Number) viewport.get("clientWidth"), (Number) viewport.get("clientHeight")
    );
  }

  private Options toOptions(Number fullWidth, Number fullHeight, Number viewWidth, Number viewHeight) {
    long width = fullWidth.longValue();
    long height = fullHeight.longValue();
    boolean exceedViewport = width > viewWidth.longValue() || height > viewHeight.longValue();
    return new Options(width, height, exceedViewport);
  }

  private record Options(long fullWidth, long fullHeight, boolean exceedViewport) {
  }
}
