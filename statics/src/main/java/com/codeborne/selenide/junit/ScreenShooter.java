package com.codeborne.selenide.junit;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
@ParametersAreNonnullByDefault
public class ScreenShooter extends TestWatcher {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooter.class);

  public boolean captureSuccessfulTests;

  private ScreenShooter() {
  }

  @CheckReturnValue
  @Nonnull
  public static ScreenShooter failedTests() {
    return new ScreenShooter();
  }

  @Nonnull
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
  @Nonnull
  public ScreenShooter to(String folderWithScreenshots) {
    Configuration.reportsFolder = folderWithScreenshots;
    return this;
  }
}
