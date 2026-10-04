package com.codeborne.selenide.cli;

/**
 * A parsed REPL command: the Java statement to record and the live action to run against the browser.
 */
record PendingCommand(RecordedStatement statement, Runnable action) {
}
