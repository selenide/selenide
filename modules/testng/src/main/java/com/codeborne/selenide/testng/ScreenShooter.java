package com.codeborne.selenide.testng;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.UIAssertionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Annotate your test class with <code>@Listeners({ ScreenShooter.class})</code>
 */
@ParametersAreNonnullByDefault
public class ScreenShooter extends ExitCodeListener {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooter.class);

  public static boolean captureSuccessfulTests;

  @Override
  public void onTestStart(ITestResult result) {
    super.onTestStart(result);

    String className = result.getMethod().getTestClass().getName();
    String methodName = result.getMethod().getMethodName();
    Screenshots.startContext(className, methodName);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    if (!(result.getThrowable() instanceof UIAssertionError)) {
      log.info(Screenshots.saveScreenshotAndPageSource());
    }

    Screenshots.finishContext();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    super.onTestSuccess(result);
    if (captureSuccessfulTests) {
      log.info(Screenshots.saveScreenshotAndPageSource());
    }
    Screenshots.finishContext();
  }
}
