package com.codeborne.selenide.logevents;

import com.codeborne.selenide.ex.SoftAssertionError;

import org.openqa.selenium.StaleElementReferenceException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErrorsCollectorTest {

  private ErrorsCollector errorsCollector;
  private LogEvent mockedLogEvent = mock(LogEvent.class);
  private LogEvent mockedInProgressEvent = mock(LogEvent.class);
  private LogEvent mockedPassedEvent = mock(LogEvent.class);
  private LogEvent mockedFailedEvent = mock(LogEvent.class);
  private Field errorsField;
  private String defaultErrorMessage = "Couldn't find an element";
  private String defaultTestName = "ITestName";


  @BeforeMethod
  public void setup() throws NoSuchFieldException {
    errorsCollector = new ErrorsCollector();
    errorsField = errorsCollector.getClass().getDeclaredField("errors");
    errorsField.setAccessible(true);
    when(mockedInProgressEvent.getStatus()).thenReturn(LogEvent.EventStatus.IN_PROGRESS);
    when(mockedPassedEvent.getStatus()).thenReturn(LogEvent.EventStatus.PASS);
    when(mockedFailedEvent.getStatus()).thenReturn(LogEvent.EventStatus.FAIL);
    when(mockedFailedEvent.getError()).thenReturn(new StaleElementReferenceException(defaultErrorMessage));

  }

  @Test
  public void testOnEvent() throws IllegalAccessException {
    List<Throwable> errors = (List<Throwable>) errorsField.get(errorsCollector);

    errorsCollector.onEvent(mockedInProgressEvent);
    assertEquals(0, errors.size());

    errorsCollector.onEvent(mockedPassedEvent);
    assertEquals(0, errors.size());

    errorsCollector.onEvent(mockedFailedEvent);
    assertEquals(1, errors.size());
    Throwable error = errors.get(0);
    assertTrue(error instanceof StaleElementReferenceException);
    assertTrue(error.getMessage().contains(defaultErrorMessage));
  }

  @Test
  public void testClearMethod() throws IllegalAccessException {
    List<Throwable> errors = (List<Throwable>) errorsField.get(errorsCollector);

    errorsCollector.onEvent(mockedFailedEvent);
    errorsCollector.onEvent(mockedFailedEvent);
    errorsCollector.onEvent(mockedFailedEvent);

    assertEquals(3, errors.size());
    errorsCollector.clear();

    assertEquals(0, errors.size());
  }


  @Test
  public void testFailIfErrorMethodWhenOnlyOneError() {
    errorsCollector.onEvent(mockedFailedEvent);
    try {
      errorsCollector.failIfErrors(defaultTestName);
    } catch (SoftAssertionError error) {
      assertTrue("I couldn't find default error message in error message",
                 error.getMessage().contains(defaultErrorMessage));
    }
  }

  @Test
  public void testFailIfErrorMethodWhenMoreThenOneError() {
    LogEvent mockedFailedEvent2 = mock(LogEvent.class);
    String failedEvent2Message = "Second failure";
    when(mockedFailedEvent2.getStatus()).thenReturn(LogEvent.EventStatus.FAIL);
    when(mockedFailedEvent2.getError()).thenReturn(new StaleElementReferenceException(failedEvent2Message));

    errorsCollector.onEvent(mockedFailedEvent);
    errorsCollector.onEvent(mockedFailedEvent2);
    try {
      errorsCollector.failIfErrors(defaultTestName);
    } catch (SoftAssertionError error) {
      String errorMessage = error.getMessage();
      assertTrue("Error title is missing",
                 errorMessage.contains(String.format("Test %s failed.", defaultTestName)));
      assertTrue("Record about number of failed checks is missing",
                 errorMessage.contains("2 checks failed"));
      assertTrue("First event message is missing",
                 errorMessage.contains(
                   String.format("FAIL #1: org.openqa.selenium.StaleElementReferenceException: %s", defaultErrorMessage)));
      assertTrue("Second event message is missing",
                 errorMessage.contains(
                   String.format("FAIL #2: org.openqa.selenium.StaleElementReferenceException: %s", failedEvent2Message)));
    }
  }
}
