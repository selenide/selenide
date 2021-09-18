package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.assertj.core.api.WithAssertions;
import org.testng.IClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
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
  private final SoftAsserts listener = new SoftAsserts();

  @AfterMethod
  void tearDown() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void findsListenersAnnotationFromParentClass() {
    assertThat(listener.getListenersAnnotation(BaseSoftTest.class)).isNotNull();
    assertThat(listener.getListenersAnnotation(SoftTest.class)).isNotNull();
    assertThat(listener.getListenersAnnotation(BaseHardTest.class)).isNotNull();
    assertThat(listener.getListenersAnnotation(HardTest.class)).isNotNull();
    assertThat(listener.getListenersAnnotation(AnotherTest.class)).isNull();
  }

  @Test
  void interceptsTestMethod_ifTestClassHasDeclaredSoftAssertListener() {
    assertThat(listener.shouldIntercept(SoftTest.class)).isTrue();
    assertThat(listener.shouldIntercept(HardTest.class)).isFalse();
  }

  @Test
  void shouldNotInterceptTestMethod_withDeclaredExpectedExceptions() throws NoSuchMethodException {
    assertThat(listener.shouldIntercept(SoftTest.class.getMethod("someTestMethod")))
      .isTrue();
    assertThat(listener.shouldIntercept(SoftTest.class.getMethod("testWithExpectedException")))
      .isFalse();
  }

  @Test
  void addsSelenideErrorListener_forMethodsThatNeedSoftAsserts() throws Exception {
    ITestResult result = mockTestResult(SoftTest.class, "someTestMethod");

    listener.addSelenideErrorListener(result);

    assertThat(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT)).isTrue();
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
    ITestResult result = mockTestResult(HardTest.class, "yetAnotherTestMethod");

    listener.addSelenideErrorListener(result);

    assertThat(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT)).isFalse();
  }

  @Test
  void marksTestAsFailed_withAssertionError_containingAllErrors() throws Exception {
    ITestResult result = mockTestResult(SoftTest.class, "someTestMethod");

    ErrorsCollector errorsCollector = mock(ErrorsCollector.class);
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    AssertionError softAssertionError = new AssertionError("fail1, fail2, fail3");
    doThrow(softAssertionError).when(errorsCollector).failIfErrors(any());

    listener.onTestFailure(result);

    verify(result).setStatus(FAILURE);
    verify(result).setThrowable(softAssertionError);
    verify(errorsCollector).failIfErrors("com.codeborne.selenide.testng.SoftAssertsTest$SoftTest.someTestMethod");
    assertThat(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT)).isFalse();
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

    ErrorsCollector errorsCollector = mock(ErrorsCollector.class);
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    doNothing().when(errorsCollector)
      .failIfErrors("com.codeborne.selenide.testng.SoftAssertsTest.SoftTest.someTestMethod");

    listener.onTestFailure(result);

    verify(result, never()).setStatus(FAILURE);
    verify(result, never()).setThrowable(any(Throwable.class));
  }

  @Listeners(SoftAsserts.class)
  private static abstract class BaseSoftTest {
  }

  private static abstract class SoftTest extends BaseSoftTest {
    @Test(expectedExceptions = IllegalArgumentException.class)
    public abstract void testWithExpectedException();

    @Test
    public abstract void someTestMethod();
  }

  @Listeners(TextReport.class)
  private static abstract class BaseHardTest {
  }

  private static abstract class HardTest extends BaseHardTest {
    @Test
    public abstract void yetAnotherTestMethod();
  }

  private static abstract class AnotherTest {
    @Test
    public abstract void yetAnotherTestMethod();
  }
}
