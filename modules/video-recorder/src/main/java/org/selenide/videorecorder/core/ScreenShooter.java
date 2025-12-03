package org.selenide.videorecorder.core;

import com.codeborne.selenide.drivercommands.WebdriversRegistry;
import com.codeborne.selenide.impl.Photographer;
import com.codeborne.selenide.impl.WebDriverInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.openqa.selenium.OutputType.BYTES;

class ScreenShooter implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooter.class);
  private final Photographer photographer = inject();
  private final AtomicLong screenshotCounter = new AtomicLong();
  private final long threadId;
  private final File screenshotsFolder;
  private final List<Screenshot> screenshots;

  ScreenShooter(long threadId, File screenshotsFolder, List<Screenshot> screenshots) {
    this.threadId = threadId;
    this.screenshots = screenshots;
    this.screenshotsFolder = screenshotsFolder;
  }

  @Override
  public void run() {
    if (Thread.interrupted()) {
      log.warn("Screen shooter thread has been interrupted");
      return;
    }

    String originalName = currentThread().getName();
    currentThread().setName("%s id:%s videoId:%s count:%s".formatted(
      originalName, threadId, screenshotsFolder.getName(), screenshots.size()));

    try {
      WebdriversRegistry.webdriver(threadId).ifPresentOrElse(driver -> {
        takeScreenshot(driver);
      }, () -> {
        log.trace("Skip taking a screenshot because webdriver is not started in thread {}", threadId);
      });
    }
    catch (UnreachableBrowserException | IllegalStateException browserHasBeenClosed) {
      log.info("Failed to take screenshot for thread {} to folder {}: {}",
        threadId, screenshotsFolder.getName(), browserHasBeenClosed.toString());
    }
    catch (org.openqa.selenium.TimeoutException timeout) {
      log.warn("Failed to take screenshot for thread {} to folder {}: {}", threadId, screenshotsFolder.getName(), timeout.toString());
    }
    catch (Throwable e) {
      log.error("Failed to take screenshot for thread {} to folder {}: {}", threadId, screenshotsFolder.getName(), e, e);
    }
    finally {
      currentThread().setName(originalName);
    }
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
    return photographer.takeScreenshot(webDriver, BYTES)
      .orElseThrow(() -> new RuntimeException("Webdriver does not support taking screenshots"));
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
