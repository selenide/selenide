package com.codeborne.selenide.logevents;

/**
 * An implementations of this interface can be registered by <b>SelenideLogger#addListener</b> <br>
 * It will be notified on each event emitted by Selenide
 *
 * @see SelenideLogger
 */
public interface LogEventListener {

  void afterEvent(LogEvent currentLog);

  void beforeEvent(LogEvent currentLog);
}
