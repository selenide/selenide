package com.codeborne.selenide.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.codeborne.selenide.WebDriverRunner.takeScreenShot;

/**
 * Usage:
 * <pre>
 * {@code
 *
 * @ Rule
 * public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();
 * }</pre>
 * <p/>
 * or
 * <pre>
 * {@code
 *
 *   @ Rule
 *   public ScreenShooter makeScreenshotOnEveryTest = ScreenShooter.failedTests().succeededTests();
 * }</pre>
 */
public class ScreenShooter extends TestWatcher {
  public boolean captureFailingTests = true;
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
  protected void failed(Throwable e, Description description) {
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
