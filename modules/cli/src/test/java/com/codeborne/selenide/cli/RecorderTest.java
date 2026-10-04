package com.codeborne.selenide.cli;

import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecorderTest {
  private final Recorder recorder = new Recorder();
  private final RecordedStatement statement = RecordedStatement.of("$(\"#x\").click();", "com.codeborne.selenide.Selenide.$");

  @Test
  void recordsExpectedStatementWhenEventPasses() {
    recorder.expect(statement);
    recorder.afterEvent(event(PASS));
    assertThat(recorder.statements()).containsExactly(statement);
  }

  @Test
  void dropsExpectedStatementWhenEventFails() {
    recorder.expect(statement);
    recorder.afterEvent(event(FAIL));
    assertThat(recorder.statements()).isEmpty();
  }

  @Test
  void commitIfPendingRecordsUnloggedCommand() {
    recorder.expect(statement);
    recorder.commitIfPending();
    assertThat(recorder.statements()).containsExactly(statement);
  }

  @Test
  void discardPendingDropsStatement() {
    recorder.expect(statement);
    recorder.discardPending();
    recorder.commitIfPending();
    assertThat(recorder.statements()).isEmpty();
  }

  @Test
  void removeLastDropsMostRecentStatement() {
    recordPass(statement);
    recordPass(RecordedStatement.of("back();", "com.codeborne.selenide.Selenide.back"));
    recorder.removeLast();
    assertThat(recorder.statements()).containsExactly(statement);
  }

  @Test
  void resetClearsEverything() {
    recordPass(statement);
    recorder.reset();
    assertThat(recorder.statements()).isEmpty();
  }

  private void recordPass(RecordedStatement statement) {
    recorder.expect(statement);
    recorder.afterEvent(event(PASS));
  }

  private static LogEvent event(EventStatus status) {
    LogEvent event = mock(LogEvent.class);
    when(event.getStatus()).thenReturn(status);
    return event;
  }
}
