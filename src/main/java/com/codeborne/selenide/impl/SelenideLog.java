package com.codeborne.selenide.impl;

import com.codeborne.selenide.impl.SelenideLogger.EventStatus;
import com.codeborne.selenide.logevents.LogEvent;

public class SelenideLog implements LogEvent {

  private String subject;
  private EventStatus status;
  private String element;

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public String getStatus() {
    return this.status.name();
  }
  
  void setStatus(EventStatus status) {
    this.status = status;
  }

  @Override
  public String getElement() {
    return this.element;
  }

  void setElement(String element) {
    this.element = element;
  }

  void setSubject(String subject) {
    this.subject = subject;
  }

}
