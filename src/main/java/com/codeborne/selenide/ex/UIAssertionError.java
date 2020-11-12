package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.openqa.selenium.WebDriverException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.causedBy;
import static com.codeborne.selenide.ex.ErrorMessages.screenshot;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;


@ParametersAreNonnullByDefault
public class UIAssertionError extends AssertionError {
  private final Driver driver;

  private String screenshot;
  public long timeoutMs;

  protected UIAssertionError(Driver driver, String message) {
    super(message);
    this.driver = driver;
  }

  protected UIAssertionError(Driver driver, String message, @Nullable Throwable cause) {
    super(message, cause);
    this.driver = driver;
  }

  @CheckReturnValue
  @Override
  public final String getMessage() {
    return super.getMessage() + uiDetails();
  }

  @CheckReturnValue
  @Override
  public final String toString() {
    return getMessage();
  }

  @CheckReturnValue
  protected String uiDetails() {
    return screenshot(driver.config(), screenshot) + timeout(timeoutMs) + causedBy(getCause());
  }

  /**
   * Get path to screenshot taken after failed test
   *
   * @return empty string if screenshots are disabled
   */
  @CheckReturnValue
  public String getScreenshot() {
    return screenshot;
  }

  @CheckReturnValue
  public static Error wrap(Driver driver, Error error, long timeoutMs) {
    return Cleanup.of.isInvalidSelectorError(error) ? error : wrapThrowable(driver, error, timeoutMs);
  }

  @CheckReturnValue
  public static Throwable wrap(Driver driver, WebDriverException error, long timeoutMs) {
    return Cleanup.of.isInvalidSelectorError(error) ? error : wrapThrowable(driver, error, timeoutMs);
  }

  @CheckReturnValue
  private static UIAssertionError wrapThrowable(Driver driver, Throwable error, long timeoutMs) {
    UIAssertionError uiError = error instanceof UIAssertionError ?
      (UIAssertionError) error : wrapToUIAssertionError(driver, error);
    uiError.timeoutMs = timeoutMs;
    uiError.screenshot = ScreenShotLaboratory.getInstance().formatScreenShotPath(driver);
    return uiError;
  }

  @CheckReturnValue
  private static UIAssertionError wrapToUIAssertionError(Driver driver, Throwable error) {
    String message = error.getClass().getSimpleName() + ": " + Cleanup.of.webdriverExceptionMessage(error.getMessage());
    return new UIAssertionError(driver, message, error);
  }
}
