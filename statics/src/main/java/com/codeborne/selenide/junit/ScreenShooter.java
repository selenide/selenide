package com.codeborne.selenide.junit;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static com.codeborne.selenide.ex.ErrorMessages.screenshot;

/**
 * Usage:
 * <pre>  {@literal @}Rule
 * public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();</pre>
 * or
 * <pre>  {@literal @}Rule
 * public ScreenShooter makeScreenshotOnEveryTest = ScreenShooter.failedTests().succeededTests();</pre>
 * or
 * <pre>  {@literal @}Rule
 * public ScreenShooter makeScreenshotOnEveryTest = ScreenShooter.failedTests().to("test-results/img/");</pre>
 */
public class ScreenShooter extends TestWatcher {
  private final Logger log = Logger.getLogger(getClass().getName());

  public boolean captureSuccessfulTests;

  private ScreenShooter() {
  }

  public static ScreenShooter failedTests() {
    return new ScreenShooter();
  }

  public ScreenShooter succeededTests() {
    captureSuccessfulTests = true;
    return this;
  }

  @Override
  protected void starting(Description test) {
    Screenshots.startContext(test.getClassName(), test.getMethodName());
  }

  @Override
  protected void succeeded(Description test) {
    if (captureSuccessfulTests) {
      log.info(screenshot(driver()));
    }
  }

  @Override
  protected void failed(Throwable e, Description description) {
    if (!(e instanceof UIAssertionError)) {
      log.info(screenshot(driver()));
    }
  }

  @Override
  protected void finished(Description description) {
    Screenshots.finishContext();
  }

  /**
   * One-liner to configure Configuration.reportsFolder property
   *
   * @param folderWithScreenshots Folder to put screenshots to
   */
  public ScreenShooter to(String folderWithScreenshots) {
    Configuration.reportsFolder = folderWithScreenshots;
    return this;
  }
}
