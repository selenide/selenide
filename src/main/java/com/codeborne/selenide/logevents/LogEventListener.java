package com.codeborne.selenide.logevents;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An implementations of this interface can be registered by <b>SelenideLogger#addListener</b> <br>
 * It will notified on each events emitted by Selenide
 *
 * @see SelenideLogger
 */
@ParametersAreNonnullByDefault
public interface LogEventListener {

  void afterEvent(LogEvent currentLog);

  void beforeEvent(LogEvent currentLog);
}
