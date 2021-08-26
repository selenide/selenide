package com.codeborne.selenide.ex;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.causedBy;
import static com.codeborne.selenide.ex.ErrorMessages.pageUrl;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;


@ParametersAreNonnullByDefault
public class UIAssertionError extends AssertionError {
  private static final Logger log = LoggerFactory.getLogger(UIAssertionError.class);
  private final Driver driver;

  private Screenshot screenshot = Screenshot.none();
  public long timeoutMs;
  public String pageUrl;

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
    return screenshot.summary() + pageUrl(pageUrl) + timeout(timeoutMs) + causedBy(getCause());
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
    UIAssertionError uiError = error instanceof UIAssertionError ?
      (UIAssertionError) error : wrapToUIAssertionError(driver, error);
    uiError.timeoutMs = timeoutMs;
    uiError.pageUrl = getPageUrl(driver);
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
  @Nullable
  private static String getPageUrl(Driver driver) {
    try {
      return driver.url();
    }
    catch (WebDriverException commandNotImplemented) {
      return null;
    }
  }

  @CheckReturnValue
  private static UIAssertionError wrapToUIAssertionError(Driver driver, Throwable error) {
    String message = error.getClass().getSimpleName() + ": " + Cleanup.of.webdriverExceptionMessage(error.getMessage());
    return new UIAssertionError(driver, message, error);
  }
}
