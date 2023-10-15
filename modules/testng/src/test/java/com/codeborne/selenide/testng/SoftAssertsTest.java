package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.testng.IClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.internal.ConstructorOrMethod;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.ITestResult.FAILURE;

public final class SoftAssertsTest {
  private final SoftAsserts listener = new SoftAsserts();

  @AfterMethod
  void tearDown() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void findsListenersAnnotationFromParentClass() {
    assertNotNull(listener.getListenersAnnotation(BaseSoftTest.class));
    assertNotNull(listener.getListenersAnnotation(SoftTest.class));
    assertNotNull(listener.getListenersAnnotation(BaseHardTest.class));
    assertNotNull(listener.getListenersAnnotation(HardTest.class));
    assertNull(listener.getListenersAnnotation(AnotherTest.class));
  }

  @Test
  void interceptsTestMethod_ifTestClassHasDeclaredSoftAssertListener() {
    assertTrue(listener.isTestClassApplicableForSoftAsserts(SoftTest.class));
    assertFalse(listener.isTestClassApplicableForSoftAsserts(HardTest.class));
  }

  @Test
  void shouldNotInterceptTestMethod_withDeclaredExpectedExceptions() throws NoSuchMethodException {
    assertTrue(listener.isTestMethodApplicableForSoftAsserts(SoftTest.class.getMethod("someTestMethod")));
    assertFalse(listener.isTestMethodApplicableForSoftAsserts(SoftTest.class.getMethod("testWithExpectedException")));
  }

  @Test
  void addsSelenideErrorListener_forMethodsThatNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(SoftTest.class, "someTestMethod");

    listener.addSelenideErrorListener(result);

    assertTrue(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  private ITestResult mockTestResult(Class<?> testClass, String methodName) throws Exception {
    ITestResult result = mock();
    IClass iClass = mock();
    when(result.getTestClass()).thenReturn(iClass);
    when(iClass.getName()).thenReturn(testClass.getName());
    doReturn(testClass).when(iClass).getRealClass();
    when(result.getName()).thenReturn(methodName);
    ConstructorOrMethod constructorOrMethod = mock();
    ITestNGMethod method = mock();
    when(result.getMethod()).thenReturn(method);
    when(method.getConstructorOrMethod()).thenReturn(constructorOrMethod);
    when(constructorOrMethod.getMethod()).thenReturn(testClass.getMethod(methodName));
    return result;
  }

  @Test
  void shouldNotAddSelenideErrorListener_forMethodsThatDoNotNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(HardTest.class, "yetAnotherTestMethod");

    listener.addSelenideErrorListener(result);

    assertFalse(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  @Test
  void marksTestAsFailed_withAssertionError_containingAllErrors() throws Exception {
    ITestResult result = mockTestResult(SoftTest.class, "someTestMethod");

    ErrorsCollector errorsCollector = mock();
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    AssertionError softAssertionError = new AssertionError("fail1, fail2, fail3");
    doReturn(softAssertionError).when(errorsCollector).cleanAndGetAssertionError(any(), any(), anyBoolean());

    listener.onTestFailure(result);

    verify(result).setStatus(FAILURE);
    verify(result).setThrowable(softAssertionError);
    verify(errorsCollector).cleanAndGetAssertionError(
      "com.codeborne.selenide.testng.SoftAssertsTest$SoftTest.someTestMethod", null, true);
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
    ITestResult result = mockTestResult(SoftTest.class, "someTestMethod");

    ErrorsCollector errorsCollector = mock();
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    doNothing().when(errorsCollector)
      .cleanAndThrowAssertionError("com.codeborne.selenide.testng.SoftAssertsTest.SoftTest.someTestMethod", null, true);

    listener.onTestFailure(result);

    verify(result, never()).setStatus(FAILURE);
    verify(result, never()).setThrowable(any(Throwable.class));
  }

  @Listeners(SoftAsserts.class)
  private abstract static class BaseSoftTest {
  }

  private abstract static class SoftTest extends BaseSoftTest {
    @Test(expectedExceptions = IllegalArgumentException.class)
    public abstract void testWithExpectedException();

    @Test
    public abstract void someTestMethod();
  }

  @Listeners(TextReport.class)
  private abstract static class BaseHardTest {
  }

  private abstract static class HardTest extends BaseHardTest {
    @Test
    public abstract void yetAnotherTestMethod();
  }

  private abstract static class AnotherTest {
    @Test
    public abstract void yetAnotherTestMethod();
  }
}
