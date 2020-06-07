package com.codeborne.selenide.logevents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.IN_PROGRESS;

@ParametersAreNonnullByDefault
public class SelenideLog implements LogEvent {

  private final long startNs;
  private long endNs;
  private final String subject;
  private final String element;
  private EventStatus status = IN_PROGRESS;
  private Throwable error;

  public SelenideLog(String element, String subject) {
    this.element = element;
    this.subject = subject;
    startNs = System.nanoTime();
  }

  @Override
  @Nonnull
  public String getSubject() {
    return this.subject;
  }

  @Override
  @Nonnull
  public EventStatus getStatus() {
    return this.status;
  }

  protected void setStatus(EventStatus status) {
    this.status = status;
    endNs = System.nanoTime();
  }

  @Override
  @Nonnull
  public String getElement() {
    return this.element;
  }

  @Override
  public long getDuration() {
    return (endNs - startNs) / 1_000_000;
  }

  @Override
  @Nullable
  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  @Override
  @Nonnull
  public String toString() {
    return String.format("$(\"%s\") %s", element, subject);
  }
}
