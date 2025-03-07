package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.SimpleReport;
import org.jspecify.annotations.Nullable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;
import org.testng.internal.ConstructorOrMethod;

import java.util.List;

import static com.codeborne.selenide.testng.Annotations.annotation;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Reports for all method of annotated class in the suite.
 * Annotate test classes to be reported with {@code @Listeners({TextReport.class})}.
 * Child classes inherit {@code @Listeners({TextReport.class})} from parent classes.
 */
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
    Class<?> testClass = consOrMethod.getDeclaringClass();
    return isClassAnnotatedWithReport(testClass);
  }

  boolean isClassAnnotatedWithReport(@Nullable Class<?> testClass) {
    if (testClass == null) return false;
    return getListeners(testClass).stream().anyMatch(this::isTextReportListener)
      || isClassAnnotatedWithReport(testClass.getSuperclass());
  }

  private List<Class<? extends ITestNGListener>> getListeners(Class<?> testClass) {
    Listeners annotation = annotation(testClass, Listeners.class);
    return annotation != null ? asList(annotation.value()) : emptyList();
  }

  private boolean isTextReportListener(Class<? extends ITestNGListener> listener) {
    return TextReport.class.isAssignableFrom(listener);
  }
}
