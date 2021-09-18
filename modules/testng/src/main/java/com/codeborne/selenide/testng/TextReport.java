package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.SimpleReport;
import com.codeborne.selenide.testng.annotations.Report;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Reports for all method of annotated class in the suite.
 * Annotate any test class in your suite with {@code @Listeners({TextReport.class})}
 * Annotate test classes to be reported with {@code @{@link Report}}
 * @since Selenide 3.6
 *
 * Use either {@link TextReport} or {@link GlobalTextReport}, never both
 */
@ParametersAreNonnullByDefault
public class TextReport implements IInvokedMethodListener {
  protected SimpleReport report = new SimpleReport();

  public static boolean onFailedTest = true;
  public static boolean onSucceededTest = true;

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    if (onFailedTest || onSucceededTest) {
      if (isClassAnnotatedWithReport(method)) {
        report.start();
      }
    }
  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    if (testResult.isSuccess() && onSucceededTest || !testResult.isSuccess() && onFailedTest) {
      if (isClassAnnotatedWithReport(method)) {
        report.finish(testResult.getName());
      }
    }
    report.clean();
  }

  private boolean isClassAnnotatedWithReport(IInvokedMethod method) {
    ConstructorOrMethod consOrMethod = method.getTestMethod().getConstructorOrMethod();
    Report annotation = consOrMethod.getDeclaringClass().getAnnotation(Report.class);
    return annotation != null;
  }

}
