package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.reporters.ExitCodeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static java.util.Arrays.asList;

/**
 * Annotate your test class with {@code @Listeners({ SoftAsserts.class})}
 */
public class SoftAsserts extends ExitCodeListener {
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

  void addSelenideErrorListener(ITestResult result) {
    if (shouldIntercept(result.getTestClass().getRealClass()) &&
        shouldIntercept(result.getMethod().getConstructorOrMethod().getMethod())) {
      SelenideLogger.addListener(LISTENER_SOFT_ASSERT, new ErrorsCollector());
    }
  }

  boolean shouldIntercept(Class testClass) {
    Listeners listenersAnnotation = getListenersAnnotation(testClass);
    return listenersAnnotation != null && asList(listenersAnnotation.value()).contains(SoftAsserts.class);
  }

  boolean shouldIntercept(Method testMethod) {
    if (testMethod == null) return false;
    
    Test annotation = testMethod.getAnnotation(Test.class);
    return annotation != null && asList(annotation.expectedExceptions()).isEmpty();
  }

  Listeners getListenersAnnotation(Class testClass) {
    Annotation annotation = testClass.getAnnotation(Listeners.class);
    return annotation != null ? (Listeners) annotation :
        testClass.getSuperclass() != null ? getListenersAnnotation(testClass.getSuperclass()) : null;
  }

  private void failIfErrors(ITestResult result) {
    ErrorsCollector errorsCollector = SelenideLogger.removeListener(LISTENER_SOFT_ASSERT);
    if (errorsCollector != null) {
      try {
        errorsCollector.failIfErrors(result.getTestClass().getName() + '.' + result.getName());
      }
      catch (AssertionError e) {
        result.setStatus(ITestResult.FAILURE);
        result.setThrowable(e);
      }
    }
  }
}
