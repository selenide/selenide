package com.codeborne.selenide.cli;

import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

/**
 * Records Selenide commands executed during a REPL session by listening to
 * {@link com.codeborne.selenide.logevents.SelenideLogger}. A statement is kept only when its command
 * completes successfully (event status {@code PASS}).
 *
 * <p>The interpreter authors the exact statement and calls {@link #expect} before running the action;
 * the SelenideLogger event then decides whether it is recorded. {@link #commitIfPending()} is a
 * fallback for the rare command that Selenide does not log.
 */
class Recorder implements LogEventListener {
  private final List<RecordedStatement> statements = new ArrayList<>();
  private RecordedStatement pending;

  void expect(RecordedStatement statement) {
    this.pending = statement;
  }

  @Override
  public void beforeEvent(LogEvent currentLog) {
    // nothing to do before a command runs
  }

  @Override
  public void afterEvent(LogEvent currentLog) {
    if (pending == null) {
      return;
    }
    if (currentLog.getStatus() == PASS) {
      statements.add(pending);
    }
    pending = null;
  }

  void commitIfPending() {
    if (pending != null) {
      statements.add(pending);
      pending = null;
    }
  }

  void discardPending() {
    pending = null;
  }

  void removeLast() {
    if (!statements.isEmpty()) {
      statements.remove(statements.size() - 1);
    }
  }

  void reset() {
    statements.clear();
    pending = null;
  }

  List<RecordedStatement> statements() {
    return Collections.unmodifiableList(statements);
  }
}
