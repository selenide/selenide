package com.codeborne.selenide.logevents;

public interface LogEventListener {

  void onEvent(LogEvent currentLog);
}
