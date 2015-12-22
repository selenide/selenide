package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

/**
 * Annotate your test class with <code>@Listeners({ SoftAsserts.class})</code>
 */
public class SoftAsserts extends ExitCodeListener {
  private final ErrorsCollector errorsCollector = new ErrorsCollector();

  public SoftAsserts() {
    SelenideLogger.addListener(errorsCollector);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    failIfErrors(result);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    super.onTestFailedButWithinSuccessPercentage(result);
    failIfErrors(result);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    super.onTestSuccess(result);
    failIfErrors(result);
  }

  @Override
  public void onConfigurationFailure(ITestResult result) {
    super.onConfigurationFailure(result);
    failIfErrors(result);
  }

  private void failIfErrors(ITestResult result) {
    errorsCollector.failIfErrors(result.getTestClass().getName() + '.' + result.getName());
  }
}
