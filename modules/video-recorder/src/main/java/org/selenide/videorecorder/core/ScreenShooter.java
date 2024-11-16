package org.selenide.videorecorder.core;

import com.codeborne.selenide.drivercommands.WebdriversRegistry;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;
import static org.openqa.selenium.OutputType.BYTES;
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
      WebDriver webDriver = driver.webDriver();
      byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(BYTES);
      long timestamp = currentTimeMillis();
      screenshots.add(new Screenshot(timestamp, webDriver.manage().window().getSize(), driver.config(), screenshot));
      log.debug("Taken a screenshot for webdriver in thread {} at {}", threadId, timestamp);
    }, () -> {
      log.debug("Skip taking a screenshot because webdriver is not started in thread {}", threadId);
    });
  }

  void finish() {
    long t1 = currentTimeMillis() + 1000;
    screenshots.add(endMarker(t1));
    log.debug("Taken a screenshot for webdriver END at {}", t1);

    long t2 = currentTimeMillis() + 3000;
    screenshots.add(endMarker(t2));
    log.debug("Taken a screenshot for webdriver END at {}", t2);
  }
}
