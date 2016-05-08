package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.*;
import com.codeborne.selenide.testng.annotations.*;
import org.testng.*;
import org.testng.internal.*;

/**
 * Reports for all method of annotated class in the suite.
 * Annotate any test class in your suite with {@code @Listeners({TextReport.class})}
 * Annotate test classes to be reported with {@code @{@link Report}}
 * @since Selenide 3.6
 *
 * Use either {@link TextReport} or {@link GlobalTextReport}, never both
 */
public class TextReport implements IInvokedMethodListener {
  protected SimpleReport report = new SimpleReport();

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    if (isClassAnnotatedWithReport(method)) {
      report.start();
    }
  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    if (isClassAnnotatedWithReport(method)) {
      report.finish(testResult.getName());
    }
  }

  private boolean isClassAnnotatedWithReport(IInvokedMethod method) {
    ConstructorOrMethod consOrMethod = method.getTestMethod().getConstructorOrMethod();
    Report annotation = consOrMethod.getDeclaringClass().getAnnotation(Report.class);
    return annotation != null;
  }

}
