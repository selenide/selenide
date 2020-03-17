package com.codeborne.selenide.logevents;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.IN_PROGRESS;

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
  public String getSubject() {
    return this.subject;
  }

  @Override
  public EventStatus getStatus() {
    return this.status;
  }
  
  protected void setStatus(EventStatus status) {
    this.status = status;
    endNs = System.nanoTime();
  }

  @Override
  public String getElement() {
    return this.element;
  }
  
  @Override
  public long getDuration() {
    return (endNs - startNs) / 1000000;
  }

  @Override
  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return String.format("$(\"%s\") %s", element, subject);
  }
}
