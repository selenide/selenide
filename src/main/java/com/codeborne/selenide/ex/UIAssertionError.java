package com.codeborne.selenide.ex;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.JavascriptErrorsCollector;
import com.codeborne.selenide.impl.ScreenShotLaboratory;

import java.util.List;

import static com.codeborne.selenide.ex.ErrorMessages.causedBy;
import static com.codeborne.selenide.ex.ErrorMessages.jsErrors;
import static com.codeborne.selenide.ex.ErrorMessages.screenshot;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;


public class UIAssertionError extends AssertionError {
  private static final JavascriptErrorsCollector javascriptErrorsCollector = new JavascriptErrorsCollector();
  private static final ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  private String screenshot;
  protected List<String> jsErrors;
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

  public static Error wrap(Context context, Error error, long timeoutMs) {
    if (Cleanup.of.isInvalidSelectorError(error))
      return error;

    return wrapThrowable(context, error, timeoutMs);
  }

  private static Error wrapThrowable(Context context, Throwable error, long timeoutMs) {
    UIAssertionError uiError = error instanceof UIAssertionError ? (UIAssertionError) error : new UIAssertionError(error);
    uiError.timeoutMs = timeoutMs;
    uiError.screenshot = screenshots.formatScreenShotPath(context);
    uiError.jsErrors = javascriptErrorsCollector.getJavascriptErrors(context);
    return uiError;
  }
}
