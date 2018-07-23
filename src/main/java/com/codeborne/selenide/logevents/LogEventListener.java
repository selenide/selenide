package com.codeborne.selenide.logevents;

/**
 * An implementations of this interface can be registered by <b>SelenideLogger#addListener</b> <br>
 * It will notified on each events emitted by Selenide
 *
 * @see SelenideLogger
 */
public interface LogEventListener {

  void onEvent(LogEvent currentLog);
}
