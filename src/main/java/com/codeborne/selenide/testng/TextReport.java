package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.*;
import com.codeborne.selenide.testng.annotations.*;
import org.testng.*;
import org.testng.internal.*;

/**
 * Annotate your test class with {@code @Listeners({ TextReport.class})}
 * @since Selenide 2.25
 */
public class TextReport implements IInvokedMethodListener {
  protected SimpleReport report = new SimpleReport();

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    if (isClassAnnotatedWithReport(method)) {
      report.start();
    }
  }

  private boolean isClassAnnotatedWithReport(IInvokedMethod method) {
    ConstructorOrMethod consOrMethod = method.getTestMethod().getConstructorOrMethod();
    Report annotation = consOrMethod.getDeclaringClass().getAnnotation(Report.class);
    return annotation != null;
  }
  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    if (isClassAnnotatedWithReport(method)) {
      report.finish(testResult.getName());
    }


  }


}
