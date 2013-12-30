package com.codeborne.selenide.testng;

import com.codeborne.selenide.Screenshots;
import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

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
    Screenshots.startContext(className, methodName);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    Screenshots.finishContext();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    super.onTestSuccess(result);
    if (captureSuccessfulTests) {
      System.out.println(screenshot());
    }
    Screenshots.finishContext();
  }
}
