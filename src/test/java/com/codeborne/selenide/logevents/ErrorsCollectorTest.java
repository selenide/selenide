package com.codeborne.selenide.logevents;

import com.codeborne.selenide.ex.SoftAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ErrorsCollectorTest {
  private final ErrorsCollector errorsCollector = new ErrorsCollector();
  private final LogEvent mockedInProgressEvent = mock();
  private final LogEvent mockedPassedEvent = mock();
  private final LogEvent mockedFailedEvent = mock();
  private final String defaultErrorMessage = "Couldn't find an element";
  private final StaleElementReferenceException defaultError = new StaleElementReferenceException(defaultErrorMessage);
  private final String defaultTestName = "ITestName";

  @BeforeEach
  void setup() {
    when(mockedInProgressEvent.getStatus()).thenReturn(LogEvent.EventStatus.IN_PROGRESS);
    when(mockedPassedEvent.getStatus()).thenReturn(LogEvent.EventStatus.PASS);
    when(mockedFailedEvent.getStatus()).thenReturn(LogEvent.EventStatus.FAIL);
    when(mockedFailedEvent.getError()).thenReturn(defaultError);
  }

  @Test
  void onEvent() {
    List<Throwable> errors = errorsCollector.getErrors();

    errorsCollector.afterEvent(mockedInProgressEvent);
    assertThat(errors)
      .hasSize(0);

    errorsCollector.afterEvent(mockedPassedEvent);
    assertThat(errors)
      .hasSize(0);

    errorsCollector.afterEvent(mockedFailedEvent);
    assertThat(errors)
      .hasSize(1);
    Throwable error = errors.get(0);
    assertThat(error)
      .isInstanceOf(StaleElementReferenceException.class);
    assertThat(error)
      .hasMessageContaining(defaultErrorMessage);
  }

  @Test
  void clearMethod() {
    List<Throwable> errors = errorsCollector.getErrors();

    errorsCollector.afterEvent(mockedFailedEvent);
    errorsCollector.afterEvent(mockedFailedEvent);
    errorsCollector.afterEvent(mockedFailedEvent);

    assertThat(errors)
      .hasSize(3);
    errorsCollector.clear();

    assertThat(errors)
      .hasSize(0);
  }

  @Test
  void failIfErrorMethodWhenOnlyOneError() {
    errorsCollector.afterEvent(mockedFailedEvent);

    assertThatThrownBy(() -> errorsCollector.cleanAndThrowAssertionError(defaultTestName, null, true))
      .isInstanceOf(SoftAssertionError.class)
      .hasMessageContaining(defaultErrorMessage);
  }

  @Test
  void failIfErrorMethodWhenMoreThanOneError() {
    LogEvent mockedFailedEvent2 = mock();
    StaleElementReferenceException failedEvent2Error = new StaleElementReferenceException("Second failure");
    when(mockedFailedEvent2.getStatus()).thenReturn(LogEvent.EventStatus.FAIL);
    when(mockedFailedEvent2.getError()).thenReturn(failedEvent2Error);

    errorsCollector.afterEvent(mockedFailedEvent);
    errorsCollector.afterEvent(mockedFailedEvent2);
    try {
      errorsCollector.cleanAndThrowAssertionError(defaultTestName, null, true);
      fail("Expected SoftAssertionError");
    }
    catch (SoftAssertionError error) {
      assertThat(error)
        .hasMessageStartingWith("Test " + defaultTestName + " failed (2 failures)");
      assertThat(error.getFailures())
        .isEqualTo(asList(defaultError, failedEvent2Error));
      assertThat(error.getSuppressed()).containsExactly(defaultError, failedEvent2Error);
    }
  }

  @Test
  void failIfErrorMethodWhenMethodThrewAnotherErrorInAdditionToSoftAsserts() {
    LogEvent mockedFailedEvent2 = mock();
    StaleElementReferenceException failedEvent2Error = new StaleElementReferenceException("Second failure");
    when(mockedFailedEvent2.getStatus()).thenReturn(LogEvent.EventStatus.FAIL);
    when(mockedFailedEvent2.getError()).thenReturn(failedEvent2Error);

    errorsCollector.afterEvent(mockedFailedEvent);
    errorsCollector.afterEvent(mockedFailedEvent2);
    AssertionError assertionError = new AssertionError("simple hamcrest assertion error");
    try {
      errorsCollector.cleanAndThrowAssertionError(defaultTestName, assertionError, false);
      fail("Expected SoftAssertionError");
    }
    catch (SoftAssertionError error) {
      assertThat(error)
        .hasMessageStartingWith("Test " + defaultTestName + " failed (3 failures)");
      assertThat(error.getFailures())
        .isEqualTo(asList(defaultError, failedEvent2Error, assertionError));
      assertThat(error.getSuppressed()).isEmpty();
    }
  }
}
