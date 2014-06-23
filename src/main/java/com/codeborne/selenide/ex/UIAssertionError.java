package com.codeborne.selenide.ex;

import java.util.List;

import static com.codeborne.selenide.Selenide.getJavascriptErrors;
import static com.codeborne.selenide.ex.ErrorMessages.*;

public class UIAssertionError extends AssertionError {
  private final String screenshot;
  private final List<String> jsErrors;
  private final String detailedMessage;
  
  protected UIAssertionError(String message) {
    super(message);
    screenshot = formatScreenShotPath();
    jsErrors = getJavascriptErrors();
    detailedMessage = message + screenshot(screenshot) + jsErrors(jsErrors);
  }

  protected UIAssertionError(String message, long timeoutMs) {
    super(message);
    screenshot = formatScreenShotPath();
    jsErrors = getJavascriptErrors();
    detailedMessage = message + screenshot(screenshot) + jsErrors(jsErrors) + timeout(timeoutMs);
  }

  protected UIAssertionError(String message, long timeoutMs, Exception cause) {
    super(message);
    screenshot = formatScreenShotPath();
    jsErrors = getJavascriptErrors();
    detailedMessage = message + screenshot(screenshot) + jsErrors(jsErrors) + timeout(timeoutMs) + causedBy(cause);
  }

  @Override
  public String getMessage() {
    return detailedMessage;
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
}
