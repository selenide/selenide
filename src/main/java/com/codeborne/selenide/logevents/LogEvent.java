package com.codeborne.selenide.logevents;

public interface LogEvent {

  String getElement();
  String getSubject();
  String getStatus();
}
