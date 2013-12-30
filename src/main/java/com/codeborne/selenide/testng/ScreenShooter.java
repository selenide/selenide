package com.codeborne.selenide.testng;

import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

import static com.codeborne.selenide.WebDriverRunner.screenshots;
import static com.codeborne.selenide.ex.ErrorMessages.screenshot;

/**
 * Annotate your test class with <code>@Listeners({ ScreenShooter.class})</code>
 */
public class ScreenShooter extends ExitCodeListener {
  public static boolean captureSuccessfulTests;

  @Override
  public void onTestStart(ITestResult result) {
    super.onTestStart(result);

    String className = result.getMethod().getTestClass().getName();
    String methodName = result.getMethod().getMethodName();
    screenshots.startContext(className, methodName);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    screenshots.endContext();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    super.onTestSuccess(result);
    if (captureSuccessfulTests) {
      System.out.println(screenshot());
    }
    screenshots.endContext();
  }
}
