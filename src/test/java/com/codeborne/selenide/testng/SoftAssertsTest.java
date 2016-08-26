package com.codeborne.selenide.testng;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import integration.AttributeTest;
import integration.testng.ReportsNGTest;
import integration.testng.SoftAssertTestNGTest1;
import integration.testng.SoftAssertTestNGTest2;
import org.junit.After;
import org.junit.Test;
import org.testng.ITestResult;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.testng.ITestResult.FAILURE;

public class SoftAssertsTest {
  SoftAsserts listener = new SoftAsserts();

  @After
  public void tearDown() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  public void findsListenersAnnotationFromParentClass() {
    assertNotNull(listener.getListenersAnnotation(SoftAssertTestNGTest1.class));
    assertNotNull(listener.getListenersAnnotation(SoftAssertTestNGTest2.class));
    assertNotNull(listener.getListenersAnnotation(ReportsNGTest.class));
    
    assertNull(listener.getListenersAnnotation(AttributeTest.class));
  }
  
  @Test
  public void interceptsTestMethod_ifTestClassHasDeclaredSoftAssertListener() {
    assertTrue(listener.shouldIntercept(SoftAssertTestNGTest1.class));
    assertNotNull(listener.shouldIntercept(SoftAssertTestNGTest2.class));
    
    assertFalse(listener.shouldIntercept(ReportsNGTest.class));
    assertFalse(listener.shouldIntercept(AttributeTest.class));
  }

  @Test
  public void addsSelenideErrorListener_forMethodsThatNeedSoftAsserts() {
    ITestResult result = mockTestResult(SoftAssertTestNGTest1.class, null);
    
    listener.addSelenideErrorListener(result);

    assertTrue(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  @Test
  public void shouldNotAddSelenideErrorListener_forMethodsThatDoNotNeedSoftAsserts() {
    ITestResult result = mockTestResult(ReportsNGTest.class, null);
    
    listener.addSelenideErrorListener(result);

    assertFalse(SelenideLogger.hasListener(LISTENER_SOFT_ASSERT));
  }

  @Test
  public void marksTestAsFailed_withAssertionError_containingAllErrors() {
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
  public void shouldNotMarkTestAsFailed_forMethodsThatDoNotNeedSoftAsserts() {
    SelenideLogger.removeListener(LISTENER_SOFT_ASSERT);
    ITestResult result = mock(ITestResult.class, RETURNS_DEEP_STUBS);
    listener.onTestFailure(result);
    verify(result, never()).setStatus(FAILURE);
    verify(result, never()).setThrowable(any(Throwable.class));
  }

  @Test
  public void shouldNotMarkTestAsFailed_ifThereWereNoErrorsDuringMethodExecution() {
    ITestResult result = mockTestResult(SoftAssertTestNGTest2.class, "userCanUseSoftAssertWithTestNG2");

    ErrorsCollector errorsCollector = mock(ErrorsCollector.class);
    SelenideLogger.addListener(LISTENER_SOFT_ASSERT, errorsCollector);
    doNothing().when(errorsCollector)
        .failIfErrors("integration.testng.SoftAssertTestNGTest2.userCanUseSoftAssertWithTestNG2");
    
    listener.onTestFailure(result);
    
    verify(result, never()).setStatus(FAILURE);
    verify(result, never()).setThrowable(any(Throwable.class));
  }

  private ITestResult mockTestResult(Class<?> testClass, String methodName) {
    ITestResult result = mock(ITestResult.class, RETURNS_DEEP_STUBS);
    when(result.getTestClass().getName()).thenReturn(testClass.getName());
    when(result.getTestClass().getRealClass()).thenReturn(testClass);
    when(result.getName()).thenReturn(methodName);
    return result;
  }
}
