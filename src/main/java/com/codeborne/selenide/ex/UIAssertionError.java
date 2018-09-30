package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;

import static com.codeborne.selenide.ex.ErrorMessages.causedBy;
import static com.codeborne.selenide.ex.ErrorMessages.screenshot;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;


public class UIAssertionError extends AssertionError {
  private final Driver driver;

  private String screenshot;
  public long timeoutMs;

  public UIAssertionError(Driver driver, Throwable cause) {
    this(driver, cause.getClass().getSimpleName() + ": " + cause.getMessage(), cause);
  }

  protected UIAssertionError(Driver driver, String message) {
    super(message);
    this.driver = driver;
  }

  protected UIAssertionError(Driver driver, String message, Throwable cause) {
    super(message, cause);
    this.driver = driver;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getLocalizedMessage() + uiDetails();
  }

  protected String uiDetails() {
    return screenshot(driver.config(), screenshot) + timeout(timeoutMs) + causedBy(getCause());
  }

  /**
   * Get path to screenshot taken after failed test
   *
   * @return empty string if screenshots are disabled
   */
  public String getScreenshot() {
    return screenshot;
  }

  public static Error wrap(Driver driver, Error error, long timeoutMs) {
    if (Cleanup.of.isInvalidSelectorError(error))
      return error;

    return wrapThrowable(driver, error, timeoutMs);
  }

  private static Error wrapThrowable(Driver driver, Throwable error, long timeoutMs) {
    UIAssertionError uiError = error instanceof UIAssertionError ? (UIAssertionError) error : new UIAssertionError(driver, error);
    uiError.timeoutMs = timeoutMs;
    uiError.screenshot = ScreenShotLaboratory.getInstance().formatScreenShotPath(driver);
    return uiError;
  }
}
