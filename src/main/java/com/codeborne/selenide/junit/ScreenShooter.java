package com.codeborne.selenide.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.codeborne.selenide.WebDriverRunner.takeScreenShot;

/**
 * Usage:
 * <code>
 *   @Rule
 *   public ScreenShooter makeScreenshotOnFailure = new ScreenShooter();
 * </code>
 *
 * or
 * <code>
 *   @Rule
 *   public ScreenShooter makeScreenshotOnEveryTest = new ScreenShooter(true. true);
 * </code>
 */
public class ScreenShooter extends TestWatcher {
  private final boolean captureFailingTests;
  private final boolean captureSuccessfulTests;

  public ScreenShooter() {
    this(true, false);
  }

  public ScreenShooter(boolean captureFailingTests, boolean captureSuccessfulTests) {
    this.captureFailingTests = captureFailingTests;
    this.captureSuccessfulTests = captureSuccessfulTests;
  }

  @Override protected void failed(Throwable e, Description description) {
    if (captureFailingTests) {
      System.err.println("Saved failed test screenshot to: " + takeScreenShot(description.getClassName() + "." + description.getMethodName()));
    }
  }

  @Override
  protected void succeeded(Description description) {
    if (captureSuccessfulTests) {
      System.err.println("Saved successful test screenshot to: " + takeScreenShot(description.getClassName() + "." + description.getMethodName()));
    }
  }
}
