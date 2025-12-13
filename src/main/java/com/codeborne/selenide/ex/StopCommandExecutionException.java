package com.codeborne.selenide.ex;

/**
 * This exception can be thrown from Commands to signal that the command execution
 * should be aborted if it lasts more than N milliseconds.
 */
public class StopCommandExecutionException extends AssertionError {
  private final long timeoutMs;

  public StopCommandExecutionException(UIAssertionError cause, long timeoutMs) {
    super(cause);
    this.timeoutMs = timeoutMs;
  }

  @Override
  public synchronized UIAssertionError getCause() {
    return (UIAssertionError) super.getCause();
  }

  public long timeoutMs() {
    return timeoutMs;
  }
}
