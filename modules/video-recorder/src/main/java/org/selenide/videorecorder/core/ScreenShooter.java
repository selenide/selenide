package org.selenide.videorecorder.core;

import com.codeborne.selenide.drivercommands.WebdriversRegistry;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Queue;
import java.util.TimerTask;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.OutputType.FILE;
import static org.selenide.videorecorder.core.Screenshot.endMarker;

class ScreenShooter extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooter.class);
  private final long threadId;
  private final Queue<Screenshot> screenshots;

  ScreenShooter(long threadId, Queue<Screenshot> screenshots) {
    this.threadId = threadId;
    this.screenshots = screenshots;
  }

  @Override
  public void run() {
    WebdriversRegistry.webdriver(threadId).ifPresentOrElse(driver -> {
      long start = nanoTime();
      log.debug("Taking a screenshot for webdriver in thread {} at {} ...", threadId, start);
      WebDriver webDriver = driver.webDriver();
      File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(FILE);
      screenshot.deleteOnExit();
      long timestamp = nanoTime();
      Dimension windowSize = screenshots.isEmpty() ? webDriver.manage().window().getSize() : new Dimension(-1, -1);
      screenshots.add(new Screenshot(start, windowSize, driver.config(), new FileImageSource(screenshot)));
      long duration = NANOSECONDS.toMillis(timestamp - start);
      log.debug("Taken a screenshot in thread {} at {} in {} ms: {}", threadId, timestamp, duration, screenshot);
    }, () -> {
      log.trace("Skip taking a screenshot because webdriver is not started in thread {}", threadId);
    });
  }

  void finish() {
    long t1 = nanoTime() + SECONDS.toNanos(1);
    screenshots.add(endMarker(t1));
    log.debug("Added an end marker at {}", t1);

    long t2 = nanoTime() + SECONDS.toNanos(3);
    screenshots.add(endMarker(t2));
    log.debug("Added an end marker at {}", t2);
  }
}
