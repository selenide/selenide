package com.codeborne.selenide.ex;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebDriverException;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.causedBy;
import static com.codeborne.selenide.ex.ErrorMessages.duration;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;


@ParametersAreNonnullByDefault
public class UIAssertionError extends AssertionFailedError {
  private static final Logger log = LoggerFactory.getLogger(UIAssertionError.class);

  private Screenshot screenshot = Screenshot.none();
  public long timeoutMs;
  public long durationMs;

  protected UIAssertionError(String message) {
    super(message);
  }

  protected UIAssertionError(String message, @Nullable Object expected, @Nullable Object actual) {
    super(message, expected, actual);
  }

  protected UIAssertionError(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

  protected UIAssertionError(String message,
                             @Nullable Object expected, @Nullable Object actual,
                             @Nullable Throwable cause) {
    super(message, expected, actual, cause);
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
    return screenshot.summary() + timeout(timeoutMs) + duration(durationMs) + causedBy(getCause());
  }

  /**
   * Get path to screenshot taken after failed test
   *
   * @return empty string if screenshots are disabled
   */
  @CheckReturnValue
  public Screenshot getScreenshot() {
    return screenshot;
  }

  @CheckReturnValue
  public static Error wrap(Driver driver, Error error, long timeoutMs) {
    return wrap(driver, error, timeoutMs, -1);
  }

  @CheckReturnValue
  public static Error wrap(Driver driver, Error error, long timeoutMs, long duration) {
    return Cleanup.of.isInvalidSelectorError(error) ? error : wrapThrowable(driver, error, timeoutMs, duration);
  }

  @CheckReturnValue
  public static Throwable wrap(Driver driver, WebDriverException error, long timeoutMs) {
    return Cleanup.of.isInvalidSelectorError(error) ? error : wrapThrowable(driver, error, timeoutMs, -1);
  }

  @CheckReturnValue
  private static UIAssertionError wrapThrowable(Driver driver, Throwable error, long timeoutMs, long duration) {
    UIAssertionError uiError = error instanceof UIAssertionError ?
      (UIAssertionError) error : wrapToUIAssertionError(error);
    uiError.timeoutMs = timeoutMs;
    uiError.durationMs = duration;
    if (uiError.screenshot.isPresent()) {
      log.warn("UIAssertionError already has screenshot: {} {} -> {}",
        uiError.getClass().getName(), uiError.getMessage(), uiError.screenshot);
    }
    else {
      Config config = driver.config();
      uiError.screenshot = ScreenShotLaboratory.getInstance()
        .takeScreenshot(driver, config.screenshots(), config.savePageSource());
    }
    return uiError;
  }

  @CheckReturnValue
  private static UIAssertionError wrapToUIAssertionError(Throwable error) {
    String message = error.getClass().getSimpleName() + ": " + Cleanup.of.webdriverExceptionMessage(error.getMessage());
    return new UIAssertionError(message, error);
  }
}
