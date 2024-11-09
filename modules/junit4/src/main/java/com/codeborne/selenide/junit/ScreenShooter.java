package com.codeborne.selenide.junit;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.UIAssertionError;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *
 * <p>
 *   Restrictions:
 * </p>
 * <p>
 *   This rule can only take screenshots for "static" webdriver managed by Selenide.
 *   It doesn't take screenshots for webdrivers created by your code, e.g. using {@code new SelenideDriver()}.
 * </p>
 */
public class ScreenShooter extends TestWatcher {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooter.class);

  public boolean captureSuccessfulTests;

  private ScreenShooter() {
  }

  public static ScreenShooter failedTests() {
    return new ScreenShooter();
  }

  @CanIgnoreReturnValue
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
      log.info(Screenshots.saveScreenshotAndPageSource());
    }
  }

  @Override
  protected void failed(Throwable e, Description description) {
    if (!(e instanceof UIAssertionError)) {
      log.info(Screenshots.saveScreenshotAndPageSource());
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
