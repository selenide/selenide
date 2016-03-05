package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.SimpleReport;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.reporters.ExitCodeListener;

/**
 * Annotate your test class with {@code @Listeners({ TextReport.class})}
 * @since Selenide 2.25
 */
public class TextReport extends ExitCodeListener {
  protected SimpleReport report = new SimpleReport();

  @Override
  public void onTestStart(ITestResult result) {

    report.start();
    report.setMethodName(result.getMethod().getMethodName());
  }

  @Override
  public void onTestFailure(ITestResult result) {
    report.finish(result.getName());

  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    report.finish(result.getName());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    report.finish(result.getName());
  }

  @Override
  public void onStart(ITestContext context) {
    report.setTestName(context.getCurrentXmlTest().getName());
  }


}
