package org.selenide.videorecorder.core;

import com.codeborne.selenide.drivercommands.WebdriversRegistry;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

  ScreenShooter(long threadId, File screenshotsFolder, Queue<Screenshot> screenshots) {
    this.threadId = threadId;
    this.screenshots = screenshots;
    this.screenshotsFolder = screenshotsFolder;
  }

  @Override
  public void run() {
    WebdriversRegistry.webdriver(threadId).ifPresentOrElse(driver -> {
      long start = nanoTime();
      log.debug("Taking a screenshot for webdriver in thread {} at {} ...", threadId, start);
      WebDriver webDriver = driver.webDriver();

      byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(BYTES);
      File screenshot = saveScreenshot(screenshotBytes);

      long timestamp = nanoTime();
      screenshots.add(new Screenshot(start, new ImageSource(screenshot)));
      long duration = NANOSECONDS.toMillis(timestamp - start);
      log.debug("Taken a screenshot in thread {} at {} in {} ms: {}", threadId, timestamp, duration, screenshot);
    }, () -> {
      log.trace("Skip taking a screenshot because webdriver is not started in thread {}", threadId);
    });
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
