package com.codeborne.selenide.logevents;

import com.codeborne.selenide.UseLocaleExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

final class SelenideLoggerTest {
  @RegisterExtension
  public static final UseLocaleExtension useLocale = new UseLocaleExtension("en");
  private static final Object[] NO_ARGS = null;

  @BeforeEach
  @AfterEach
  void setUp() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void convertsJavaMethodNameToHumanReadableClause() {
    assertThat(SelenideLogger.readableMethodName("click"))
      .isEqualTo("click");
    assertThat(SelenideLogger.readableMethodName("setValue"))
      .isEqualTo("set value");
    assertThat(SelenideLogger.readableMethodName("shouldBe"))
      .isEqualTo("should be");
    assertThat(SelenideLogger.readableMethodName("convertsJavaMethodNameToHumanReadableClause"))
      .isEqualTo("converts java method name to human readable clause");
  }

  @Test
  void canAddManyListenersPerThread() {
    LogEventListener listener1 = mock();
    LogEventListener listener2 = mock();
    LogEventListener listener3 = mock();

    SelenideLogger.addListener("simpleReport", listener1);
    SelenideLogger.addListener("softAsserts", listener2);
    SelenideLogger.addListener("userProvided", listener3);

    SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click", NO_ARGS), PASS);

    verifyEvent(listener1, "div", "click()", PASS);
    verifyEvent(listener2, "div", "click()", PASS);
    verifyEvent(listener3, "div", "click()", PASS);

    verifyNoMoreInteractions(listener1, listener2, listener3);

    reset(listener1, listener2, listener3);
    SelenideLogger.removeListener("simpleReport");
    SelenideLogger.removeListener("softAsserts");

    SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click", NO_ARGS), PASS);
    verifyEvent(listener3, "div", "click()", PASS);

    verifyNoMoreInteractions(listener1, listener2, listener3);
  }

  @Test
  void doesNotFail_ifSomeOfListeners_before_throwsException() {
    LogEventListener listener1 = mock();
    doThrow(new IllegalStateException("Failed to take screenshot because browser is not opened yet"))
      .when(listener1).beforeEvent(any());
    SelenideLogger.addListener("allureListener", listener1);

    SelenideLogger.commitStep(SelenideLogger.beginStep("open", "https://any.url"), FAIL);

    verifyEvent(listener1, "open", "https://any.url", FAIL);
    verifyNoMoreInteractions(listener1);
  }

  @Test
  void doesNotFail_ifSomeOfListeners_after_throwsException() {
    LogEventListener listener1 = mock();
    doThrow(new IllegalStateException("Failed to take screenshot because browser is not opened yet"))
      .when(listener1).afterEvent(any());
    SelenideLogger.addListener("allureListener", listener1);

    SelenideLogger.commitStep(SelenideLogger.beginStep("open", "https://any.url"), FAIL);

    verifyEvent(listener1, "open", "https://any.url", FAIL);
    verifyNoMoreInteractions(listener1);
  }

  @Test
  void runExecutesRunnableAndCommitsEvent() {
    LogEventListener listener = mock();
    var executed = new AtomicBoolean();

    SelenideLogger.addListener("simpleReport", listener);

    SelenideLogger.run("some-source", "some-subject", () -> executed.set(true));

    assertThat(executed.get()).isTrue();

    verifyEvent(listener, "some-source", "some-subject", PASS);
  }

  @Test
  void runCommitsFailureAndRethrowsInCaseOfException() {
    LogEventListener listener = mock();
    var exception = new RuntimeException();

    SelenideLogger.addListener("simpleReport", listener);

    assertThatThrownBy(() -> SelenideLogger.run("some-source", "some-subject", () -> {
      throw exception;
    })).isSameAs(exception);

    verifyEvent(listener, "some-source", "some-subject", FAIL);
  }

  @Test
  void getExecutesSupplierAndCommitsEvent() {
    LogEventListener listener = mock();

    SelenideLogger.addListener("simpleReport", listener);

    var executed = SelenideLogger.get("some-source", "some-subject", () -> true);

    assertThat(executed).isTrue();

    verifyEvent(listener, "some-source", "some-subject", PASS);
  }

  @Test
  void getCommitsFailureAndRethrowsInCaseOfException() {
    LogEventListener listener = mock();
    var exception = new RuntimeException();

    SelenideLogger.addListener("simpleReport", listener);

    assertThatThrownBy(() -> SelenideLogger.get("some-source", "some-subject", () -> {
      throw exception;
    })).isSameAs(exception);

    verifyEvent(listener, "some-source", "some-subject", FAIL);
  }

  @Test
  void stepExecutesSupplierAndCommitsEventWithEmptySubject() {
    LogEventListener listener = mock();

    SelenideLogger.addListener("simpleReport", listener);

    var executed = SelenideLogger.step("some-source", () -> true);

    assertThat(executed).isTrue();

    verifyEvent(listener, "some-source", "", PASS);
  }

  @Test
  void stepCommitsFailureWithEmptySubjectAndRethrowsInCaseOfException() {
    LogEventListener listener = mock();
    var exception = new RuntimeException();

    SelenideLogger.addListener("simpleReport", listener);

    assertThatThrownBy(() -> SelenideLogger.step("some-source", () -> {
      throw exception;
    })).isSameAs(exception);

    verifyEvent(listener, "some-source", "", FAIL);
  }

  @Test
  void stepExecutesRunnableAndCommitsEvent() {
    LogEventListener listener = mock();
    var executed = new AtomicBoolean();

    SelenideLogger.addListener("simpleReport", listener);

    SelenideLogger.step("some-source", () -> executed.set(true));

    assertThat(executed.get()).isTrue();

    verifyEvent(listener, "some-source", "", PASS);
  }

  @Test
  void stepCommitsFailureAndRethrowsInCaseOfException() {
    LogEventListener listener = mock();
    var exception = new RuntimeException();

    SelenideLogger.addListener("simpleReport", listener);

    assertThatThrownBy(() -> SelenideLogger.step("some-source", (Runnable) () -> {
      throw exception;
    })).isSameAs(exception);

    verifyEvent(listener, "some-source", "", FAIL);
  }

  private void verifyEvent(LogEventListener listener, String element, String subject, LogEvent.EventStatus status) {
    ArgumentCaptor<LogEvent> event = ArgumentCaptor.forClass(LogEvent.class);
    verify(listener).beforeEvent(event.capture());
    verify(listener).afterEvent(event.capture());
    LogEvent value = event.getValue();
    assertThat(value.getElement()).isEqualTo(element);
    assertThat(value.getSubject()).isEqualTo(subject);
    assertThat(value.getStatus()).isEqualTo(status);
  }
}
