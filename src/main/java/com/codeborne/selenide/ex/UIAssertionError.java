package com.codeborne.selenide.ex;

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
  public String getMessage() {
    takeCurrentSnapshot();
    return super.getMessage() + screenshot(screenshot) + jsErrors(jsErrors) + ErrorMessages.timeout(timeoutMs) + causedBy(getCause());
  }

  /**
   * Get path to screenshot taken after failed test
   * 
   * @return empty string if screenshots are disabled  
   */
  public String getScreenshot() {
    takeCurrentSnapshot();
    return screenshot;
  }

  /**
   * Get all javascript errors found during test execution
   * 
   * @return empty list if no errors found 
   */
  public List<String> getJsErrors() {
    takeCurrentSnapshot();
    return jsErrors;
  }

  private void takeCurrentSnapshot() {
    if (screenshot == null)
      screenshot = formatScreenShotPath();
    if (jsErrors == null)
      jsErrors = getJavascriptErrors();
  }
}
