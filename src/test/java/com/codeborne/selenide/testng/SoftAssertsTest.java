package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import integration.AttributeTest;
import integration.testng.ReportsNGTest;
import integration.testng.SoftAssertTestNGTest1;
import integration.testng.SoftAssertTestNGTest2;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testng.IClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.ITestResult.FAILURE;

final class SoftAssertsTest implements WithAssertions {
  private SoftAsserts listener = new SoftAsserts();

  @AfterEach
  void tearDown() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void findsListenersAnnotationFromParentClass() {
    assertThat(listener.getListenersAnnotation(SoftAssertTestNGTest1.class))
      .isNotNull();
    assertThat(listener.getListenersAnnotation(SoftAssertTestNGTest2.class))
      .isNotNull();
    assertThat(listener.getListenersAnnotation(ReportsNGTest.class))
      .isNotNull();
    assertThat(listener.getListenersAnnotation(AttributeTest.class))
      .isNull();
  }

  @Test
  void interceptsTestMethod_ifTestClassHasDeclaredSoftAssertListener() {
    assertThat(listener.shouldIntercept(SoftAssertTestNGTest1.class))
      .isTrue();
    assertThat(listener.shouldIntercept(SoftAssertTestNGTest2.class))
      .isTrue();

    assertThat(listener.shouldIntercept(ReportsNGTest.class))
      .isFalse();
    assertThat(listener.shouldIntercept(AttributeTest.class))
      .isFalse();
  }

  @Test
  void shouldNotInterceptTestMethod_withDeclaredExceptedExceptions() throws NoSuchMethodException {
    assertThat(listener.shouldIntercept(SoftAssertTestNGTest1.class.getMethod("successfulTest1")))
      .isTrue();
    assertThat(listener.shouldIntercept(SoftAssertTestNGTest1.class.getMethod("testWithExpectedExceptions")))
      .isFalse();
  }

  @Test
  void addsSelenideErrorListener_forMethodsThatNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(SoftAssertTestNGTest1.class, "successfulTest1");

    listener.addSelenideErrorListener(result);

    assertThat(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT))
      .isTrue();
  }

  private ITestResult mockTestResult(Class<?> testClass, String methodName) throws Exception {
    ITestResult result = mock(ITestResult.class);
    IClass iClass = mock(IClass.class);
    when(result.getTestClass()).thenReturn(iClass);
    when(iClass.getName()).thenReturn(testClass.getName());
    doReturn(testClass).when(iClass).getRealClass();
    when(result.getName()).thenReturn(methodName);
    ConstructorOrMethod constructorOrMethod = mock(ConstructorOrMethod.class);
    ITestNGMethod method = mock(ITestNGMethod.class);
    when(result.getMethod()).thenReturn(method);
    when(method.getConstructorOrMethod()).thenReturn(constructorOrMethod);
    when(constructorOrMethod.getMethod()).thenReturn(testClass.getMethod(methodName));
    return result;
  }

  @Test
  void shouldNotAddSelenideErrorListener_forMethodsThatDoNotNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(ReportsNGTest.class, "successfulMethod");

    listener.addSelenideErrorListener(result);

    assertThat(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT))
      .isFalse();
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

    assertThat(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT))
      .isFalse();
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
