package com.codeborne.selenide.junit;

import com.codeborne.selenide.Screenshots;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.codeborne.selenide.ex.ErrorMessages.screenshot;

/**
 * Usage:
 * <pre>  {@literal @}Rule
 * public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();</pre>
 * or
 * <pre>  {@literal @}Rule
 * public ScreenShooter makeScreenshotOnEveryTest = ScreenShooter.failedTests().succeededTests();</pre>
 */
public class ScreenShooter extends TestWatcher {
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
      System.out.println(screenshot());
    }
  }

  @Override
  protected void finished(Description description) {
    Screenshots.finishContext();
  }
}
