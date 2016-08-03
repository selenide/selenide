package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

/**
 * Annotate your test class with {@code @Listeners({ SoftAsserts.class})}
 */
public class SoftAsserts extends ExitCodeListener {
  @Override
  public void onTestStart(ITestResult result) {
    super.onTestStart(result);
    SelenideLogger.addListener("softAssert", new ErrorsCollector());
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
    ErrorsCollector errorsCollector = SelenideLogger.removeListener("softAssert");
    errorsCollector.failIfErrors(result.getTestClass().getName() + '.' + result.getName());
  }
}
