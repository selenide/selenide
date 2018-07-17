package com.codeborne.selenide.logevents;

/**
 * Events, created on Selenide actions
 * like "navigate to url", "click on element", "check a condition" <br><br>
 *
 * An event contains a string representation of the element, the subject and its status.
 */
public interface LogEvent {

  enum EventStatus {
    IN_PROGRESS, PASS, FAIL
  }

  String getElement();
  String getSubject();
  EventStatus getStatus();
  long getDuration();
  Throwable getError();
}
