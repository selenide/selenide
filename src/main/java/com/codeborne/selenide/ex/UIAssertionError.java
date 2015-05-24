package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.Cleanup;

import java.util.List;

import static com.codeborne.selenide.Selenide.getJavascriptErrors;
import static com.codeborne.selenide.ex.ErrorMessages.*;

public class UIAssertionError extends AssertionError {
  private String screenshot;
  private List<String> jsErrors;
  public long timeoutMs;

  public UIAssertionError(Throwable cause) {
    this(cause.getClass().getSimpleName() + ": " + cause.getMessage(), cause);
  }

  protected UIAssertionError(String message) {
    super(message);
  }
  
  protected UIAssertionError(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getLocalizedMessage() + uiDetails();
  }

  protected String uiDetails() {
    return screenshot(screenshot) + jsErrors(jsErrors) + timeout(timeoutMs) + causedBy(getCause());
  }

  /**
   * Get path to screenshot taken after failed test
   * 
   * @return empty string if screenshots are disabled  
   */
  public String getScreenshot() {
    return screenshot;
  }

  /**
   * Get all javascript errors found during test execution
   * 
   * @return empty list if no errors found 
   */
  public List<String> getJsErrors() {
    return jsErrors;
  }

  private void collectCurrentWebdriverData() {
    screenshot = formatScreenShotPath();
    jsErrors = getJavascriptErrors();
  }

  public static Error wrap(Error error) {
    if (Cleanup.of.isInvalidSelectorError(error))
      return error;

    UIAssertionError uiError = error instanceof UIAssertionError ? (UIAssertionError) error : new UIAssertionError(error);
    uiError.collectCurrentWebdriverData();
    return uiError;
  }
}
