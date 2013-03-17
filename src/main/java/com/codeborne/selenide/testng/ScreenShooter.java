package com.codeborne.selenide.testng;

import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

import static com.codeborne.selenide.WebDriverRunner.takeScreenShot;

/**
 * Annotate your test class with <code>@Listeners({ ScreenShooter.class})</code>
 */
public class ScreenShooter extends ExitCodeListener {
  public static boolean captureFailingTests = true;
  public static boolean captureSuccessfulTests;

  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    if (captureFailingTests) {
      System.err.println("Saved failed test screenshot to: " + takeScreenShot(getFileName(result)));
    }
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    super.onTestSuccess(result);
    if (captureSuccessfulTests) {
      System.out.println("Saved succeeded test screenshot to: " + takeScreenShot(getFileName(result)));
    }
  }

  protected String getFileName(ITestResult result) {
    String className = result.getMethod().getTestClass().getName();
    String methodName = result.getMethod().getMethodName();
    return className + "." + methodName;
  }
}
