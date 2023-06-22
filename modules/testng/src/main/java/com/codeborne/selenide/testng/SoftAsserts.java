package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.logevents.SoftAssertsErrorsCollector;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.reporters.ExitCodeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static java.util.Arrays.asList;

/**
 * Annotate your test class with {@code @Listeners({ SoftAsserts.class})}
 */
@ParametersAreNonnullByDefault
public class SoftAsserts extends ExitCodeListener {
  public static boolean fullStacktraces = true;

  @Override
  public void onTestStart(ITestResult result) {
    addSelenideErrorListener(result);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    failIfErrors(result);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    failIfErrors(result);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    failIfErrors(result);
  }

  @Override
  public void onConfigurationFailure(ITestResult result) {
    failIfErrors(result);
  }

  @Override
  public void beforeConfiguration(ITestResult result) {
    addSelenideErrorListener(result);
  }

  void addSelenideErrorListener(ITestResult result) {
    if (!SelenideLogger.hasListener(LISTENER_SOFT_ASSERT) &&
      isTestClassApplicableForSoftAsserts(result) &&
      isTestMethodApplicableForSoftAsserts(result)) {
      SelenideLogger.addListener(LISTENER_SOFT_ASSERT, new SoftAssertsErrorsCollector());
    }
  }

  private boolean isTestClassApplicableForSoftAsserts(ITestResult result) {
    return isTestClassApplicableForSoftAsserts(result.getTestClass().getRealClass());
  }

  boolean isTestClassApplicableForSoftAsserts(Class<?> testClass) {
    Listeners listenersAnnotation = getListenersAnnotation(testClass);
    return listenersAnnotation != null && asList(listenersAnnotation.value()).contains(SoftAsserts.class);
  }

  private boolean isTestMethodApplicableForSoftAsserts(ITestResult result) {
    return isTestMethodApplicableForSoftAsserts(result.getMethod().getConstructorOrMethod().getMethod());
  }

  boolean isTestMethodApplicableForSoftAsserts(@Nullable Method testMethod) {
    if (testMethod == null) return false;

    Test annotation = testMethod.getAnnotation(Test.class);
    return annotation == null || asList(annotation.expectedExceptions()).isEmpty();
  }

  Listeners getListenersAnnotation(Class<?> testClass) {
    Listeners annotation = testClass.getAnnotation(Listeners.class);
    return annotation != null ? annotation :
      testClass.getSuperclass() != null ? getListenersAnnotation(testClass.getSuperclass()) : null;
  }

  private void failIfErrors(ITestResult result) {
    ErrorsCollector errorsCollector = SelenideLogger.removeListener(LISTENER_SOFT_ASSERT);
    if (errorsCollector != null) {
      AssertionError assertionError = errorsCollector.cleanAndGetAssertionError(
        testName(result), result.getThrowable(), fullStacktraces
      );
      if (assertionError != null) {
        result.setStatus(ITestResult.FAILURE);
        result.setThrowable(assertionError);
      }
    }
  }

  @Nonnull
  private String testName(ITestResult result) {
    return result.getTestClass().getName() + '.' + result.getName();
  }
}
