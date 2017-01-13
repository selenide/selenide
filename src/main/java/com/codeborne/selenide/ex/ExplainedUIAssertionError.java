package com.codeborne.selenide.ex;

import java.util.List;

public class ExplainedUIAssertionError extends UIAssertionError {

  private final UIAssertionError delegate;

  public ExplainedUIAssertionError(String message, UIAssertionError delegate) {
    super(delegate.getMessage() + " (because " + message + ")");
    this.delegate = delegate;
    this.timeoutMs = delegate.timeoutMs;
  }

  @Override
  protected String uiDetails() {
    return delegate.uiDetails();
  }

  @Override
  public String getScreenshot() {
    return delegate.getScreenshot();
  }

  @Override
  public List<String> getJsErrors() {
    return delegate.getJsErrors();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
