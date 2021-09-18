package com.codeborne.selenide.testng;

import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

/**
 * Annotate your test class with <code>@Listeners({ BrowserPerTest.class})</code>
 */
@ParametersAreNonnullByDefault
public class BrowserPerTest extends ExitCodeListener {

  @Override
  public void onTestFailure(ITestResult result) {
    super.onTestFailure(result);
    closeWebDriver();
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    super.onTestFailedButWithinSuccessPercentage(result);
    closeWebDriver();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    super.onTestSuccess(result);
    closeWebDriver();
  }

  @Override
  public void onConfigurationFailure(ITestResult result) {
    super.onConfigurationFailure(result);
    closeWebDriver();
  }
}
