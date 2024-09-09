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

import static com.codeborne.selenide.ex.Strings.join;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class UIAssertionError extends AssertionFailedError {
  private static final Logger log = LoggerFactory.getLogger(UIAssertionError.class);
  protected static final ErrorFormatter errorFormatter = inject(ErrorFormatter.class);

  private Screenshot screenshot = Screenshot.none();
  private long timeoutMs;
  private final String initialErrorMessage;
  private String detailedErrorMessage;

  protected UIAssertionError(String message) {
    super(message);
    initialErrorMessage = message;
  }

  public UIAssertionError(String message, @Nullable Object expected, @Nullable Object actual) {
    super(message, expected, actual);
    initialErrorMessage = message;
  }

  protected UIAssertionError(String message, @Nullable Object expected, @Nullable Object actual, long timeoutMs) {
    super(message, expected, actual);
    this.timeoutMs = timeoutMs;
    initialErrorMessage = message;
  }

  protected UIAssertionError(String message, @Nullable Throwable cause) {
    super(message, cause);
    initialErrorMessage = message;
  }

  protected UIAssertionError(String message, long timeoutMs, @Nullable Throwable cause) {
    super(message, cause);
    this.timeoutMs = timeoutMs;
    initialErrorMessage = message;
  }

  protected UIAssertionError(String message,
                             @Nullable Object expected, @Nullable Object actual,
                             @Nullable Throwable cause) {
    super(message, expected, actual, cause);
    initialErrorMessage = message;
  }

  protected UIAssertionError(String message,
                             @Nullable Object expected, @Nullable Object actual,
                             @Nullable Throwable cause,
                             long timeoutMs) {
    super(message, expected, actual, cause);
    this.timeoutMs = timeoutMs;
    initialErrorMessage = message;
  }

  @CheckReturnValue
  @Override
  public final String getMessage() {
    if (detailedErrorMessage == null) {
      // if e.getMessage() was occasionally called before wrapThrowable()
      return join(initialErrorMessage, errorFormatter.causedBy(getCause()));
    }
    return detailedErrorMessage;
  }

  @CheckReturnValue
  @Override
  public final String toString() {
    return getMessage();
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
    return Cleanup.of.isInvalidSelectorError(error) ? error : wrapThrowable(driver, error, timeoutMs);
  }

  @CheckReturnValue
  public static Throwable wrap(Driver driver, WebDriverException error, long timeoutMs) {
    return Cleanup.of.isInvalidSelectorError(error) ? error : wrapThrowable(driver, error, timeoutMs);
  }

  @CheckReturnValue
  private static UIAssertionError wrapThrowable(Driver driver, Throwable error, long timeoutMs) {
    UIAssertionError uiError = error instanceof UIAssertionError uiAssertionError ?
      uiAssertionError : wrapToUIAssertionError(error);
    uiError.timeoutMs = timeoutMs;
    if (uiError.screenshot.isPresent()) {
      log.warn("UIAssertionError already has screenshot: {} {} -> {}",
        uiError.getClass().getName(), uiError.getMessage(), uiError.screenshot);
    }
    else {
      Config config = driver.config();
      uiError.screenshot = ScreenShotLaboratory.getInstance()
        .takeScreenshot(driver, config.screenshots(), config.savePageSource());
      uiError.detailedErrorMessage = join(uiError.initialErrorMessage,
        errorFormatter.generateErrorDetails(uiError, driver, uiError.screenshot, uiError.timeoutMs));
    }
    return uiError;
  }

  @CheckReturnValue
  private static UIAssertionError wrapToUIAssertionError(Throwable error) {
    String message = Cleanup.of.webdriverExceptionMessage(error);
    return new UIAssertionError(message, error);
  }
}
