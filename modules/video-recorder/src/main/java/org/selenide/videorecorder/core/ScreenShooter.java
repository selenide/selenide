package org.selenide.videorecorder.core;

import com.codeborne.selenide.drivercommands.WebdriversRegistry;
import com.codeborne.selenide.impl.WebDriverInstance;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v138.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.openqa.selenium.OutputType.BYTES;

class ScreenShooter extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooter.class);
  private final AtomicLong screenshotCounter = new AtomicLong();
  private final long threadId;
  private final File screenshotsFolder;
  private final Queue<Screenshot> screenshots;
  private volatile boolean cancelled;

  ScreenShooter(long threadId, File screenshotsFolder, Queue<Screenshot> screenshots) {
    this.threadId = threadId;
    this.screenshots = screenshots;
    this.screenshotsFolder = screenshotsFolder;
  }

  @Override
  public boolean cancel() {
    cancelled = true;
    return super.cancel();
  }

  @Override
  public void run() {
    if (cancelled) {
      log.warn("Screen shooter has been cancelled");
      return;
    }
    if (Thread.interrupted()) {
      log.warn("Screen shooter thread has been interrupted");
      return;
    }
    WebdriversRegistry.webdriver(threadId).ifPresentOrElse(driver -> {
      takeScreenshot(driver);
    }, () -> {
      log.trace("Skip taking a screenshot because webdriver is not started in thread {}", threadId);
    });
  }

  private void takeScreenshot(WebDriverInstance driver) {
    long start = nanoTime();
    log.debug("Taking a screenshot for webdriver in thread {} at {} ...", threadId, start);

    WebDriver webDriver = driver.webDriver();
    byte[] screenshotBytes = takeScreenshot(webDriver);
    File screenshot = saveScreenshot(screenshotBytes);
    long timestamp = nanoTime();
    screenshots.add(new Screenshot(start, new ImageSource(screenshot)));
    long duration = NANOSECONDS.toMillis(timestamp - start);
    log.debug("Taken a screenshot in thread {} at {} in {} ms: {}", threadId, timestamp, duration, screenshot);
  }

  private byte[] takeScreenshot(WebDriver webDriver) {
    if (webDriver instanceof HasDevTools hasDevTools) { // Chromium - HasDevTools is the fastest way
      return takeScreenshotWithDevtools((WebDriver & HasDevTools) hasDevTools);
    }
    else if (webDriver instanceof HasBiDi hasBiDi) { // Firefox - BiDi is the fastest
      return takeScreenshotWithBidi((WebDriver & HasBiDi) hasBiDi);
    }
    else { // other browsers
      return takeScreenshotWithWebdriver(webDriver);
    }
  }

  private <T extends WebDriver & HasDevTools> byte[] takeScreenshotWithDevtools(T driver) {
    DevTools devTools = driver.getDevTools();
    devTools.createSessionIfThereIsNotOne(driver.getWindowHandle());

    String base64 = devTools.send(Page.captureScreenshot(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(true)
      )
    );

    return BYTES.convertFromBase64Png(base64);
  }

  private static byte[] takeScreenshotWithWebdriver(WebDriver webDriver) {
    return ((TakesScreenshot) webDriver).getScreenshotAs(BYTES);
  }

  private <T extends WebDriver & HasBiDi> byte[] takeScreenshotWithBidi(T webDriver) {
    String windowHandle = webDriver.getWindowHandle();
    BrowsingContext browsingContext = new BrowsingContext(webDriver, windowHandle);
    String screenshotBase64 = browsingContext.captureScreenshot();
    return BYTES.convertFromBase64Png(screenshotBase64);
  }

  private File saveScreenshot(byte[] screenshot) {
    return saveScreenshot(new ByteArrayInputStream(screenshot));
  }

  private File saveScreenshot(InputStream screenshot) {
    File file = new File(screenshotsFolder, "screenshot.%s.png".formatted(screenshotCounter.incrementAndGet()));
    try {
      copyInputStreamToFile(screenshot, file);
      return file;
    } catch (IOException e) {
      throw new RuntimeException("Failed to save screenshot to %s".formatted(file.getAbsolutePath()), e);
    }
  }
}
