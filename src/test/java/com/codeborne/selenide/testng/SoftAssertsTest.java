package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import integration.AttributeTest;
import integration.testng.ReportsNGTest;
import integration.testng.SoftAssertTestNGTest1;
import integration.testng.SoftAssertTestNGTest2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testng.ITestResult;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.ITestResult.FAILURE;

class SoftAssertsTest {
  private SoftAsserts listener = new SoftAsserts();

  @AfterEach
  void tearDown() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void findsListenersAnnotationFromParentClass() {
    assertNotNull(listener.getListenersAnnotation(SoftAssertTestNGTest1.class));
    assertNotNull(listener.getListenersAnnotation(SoftAssertTestNGTest2.class));
    assertNotNull(listener.getListenersAnnotation(ReportsNGTest.class));

    assertNull(listener.getListenersAnnotation(AttributeTest.class));
  }

  @Test
  void interceptsTestMethod_ifTestClassHasDeclaredSoftAssertListener() {
    assertTrue(listener.shouldIntercept(SoftAssertTestNGTest1.class));
    assertTrue(listener.shouldIntercept(SoftAssertTestNGTest2.class));

    assertFalse(listener.shouldIntercept(ReportsNGTest.class));
    assertFalse(listener.shouldIntercept(AttributeTest.class));
  }

  @Test
  void shouldNotInterceptTestMethod_withDeclaredExceptedExceptions() throws NoSuchMethodException {
    assertTrue(listener.shouldIntercept(SoftAssertTestNGTest1.class.getMethod("successfulTest1")));
    assertFalse(listener.shouldIntercept(SoftAssertTestNGTest1.class.getMethod("testWithExpectedExceptions")));
  }

  @Test
  void addsSelenideErrorListener_forMethodsThatNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(SoftAssertTestNGTest1.class, "successfulTest1");

    listener.addSelenideErrorListener(result);

    assertTrue(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  private ITestResult mockTestResult(Class<?> testClass, String methodName) throws Exception {
    ITestResult result = mock(ITestResult.class, RETURNS_DEEP_STUBS);
    when(result.getTestClass().getName()).thenReturn(testClass.getName());
    when(result.getTestClass().getRealClass()).thenReturn(testClass);
    when(result.getName()).thenReturn(methodName);
    when(result.getMethod().getConstructorOrMethod().getMethod()).thenReturn(testClass.getMethod(methodName));
    return result;
  }

  @Test
  void shouldNotAddSelenideErrorListener_forMethodsThatDoNotNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(ReportsNGTest.class, "successfulMethod");

    listener.addSelenideErrorListener(result);

    assertFalse(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  @Test
  void marksTestAsFailed_withAssertionError_containingAllErrors() throws Exception {
    ITestResult result = mockTestResult(SoftAssertTestNGTest2.class, "userCanUseSoftAssertWithTestNG2");

    ErrorsCollector errorsCollector = mock(ErrorsCollector.class);
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    AssertionError softAssertionError = new AssertionError("fail1, fail2, fail3");
    doThrow(softAssertionError)
      .when(errorsCollector).failIfErrors("integration.testng.SoftAssertTestNGTest2.userCanUseSoftAssertWithTestNG2");

    listener.onTestFailure(result);

    verify(result).setStatus(FAILURE);
    verify(result).setThrowable(softAssertionError);

    assertFalse(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  @Test
  void shouldNotMarkTestAsFailed_forMethodsThatDoNotNeedSoftAsserts() {
    SelenideLogger.removeListener(LISTENER_SOFT_ASSERT);
    ITestResult result = mock(ITestResult.class, RETURNS_DEEP_STUBS);
    listener.onTestFailure(result);
    verify(result, never()).setStatus(FAILURE);
    verify(result, never()).setThrowable(any(Throwable.class));
  }

  @Test
  void shouldNotMarkTestAsFailed_ifThereWereNoErrorsDuringMethodExecution() throws Exception {
    ITestResult result = mockTestResult(SoftAssertTestNGTest2.class, "userCanUseSoftAssertWithTestNG2");

    ErrorsCollector errorsCollector = mock(ErrorsCollector.class);
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    doNothing().when(errorsCollector)
      .failIfErrors("integration.testng.SoftAssertTestNGTest2.userCanUseSoftAssertWithTestNG2");

    listener.onTestFailure(result);

    verify(result, never()).setStatus(FAILURE);
    verify(result, never()).setThrowable(any(Throwable.class));
  }
}
