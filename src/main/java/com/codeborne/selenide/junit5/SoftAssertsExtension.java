package com.codeborne.selenide.junit5;

import com.codeborne.selenide.logevents.ErrorsCollector;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.logevents.ErrorsCollector.LISTENER_SOFT_ASSERT;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static com.codeborne.selenide.logevents.SelenideLogger.removeListener;

/**
 * @author Aliaksandr Rasolka
 * @since 4.12.2
 */
public class SoftAssertsExtension implements BeforeEachCallback, AfterEachCallback {
  private final ErrorsCollector errorsCollector;

  /**
   * Create SoftAssertsExtension instance with empty ErrorsCollector object.
   * <br/>
   * Used during extension registration.
   *
   * @see ErrorsCollector
   */
  public SoftAssertsExtension() {
    errorsCollector = new ErrorsCollector();
  }

  /**
   * Return error collector object.
   *
   * @return ErrorsCollector instance
   *
   * @see ErrorsCollector
   */
  public ErrorsCollector getErrorsCollector() {
    return errorsCollector;
  }

  @Override
  public void beforeEach(final ExtensionContext context) {
    addListener(LISTENER_SOFT_ASSERT, errorsCollector);
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    removeListener(LISTENER_SOFT_ASSERT);
    errorsCollector.failIfErrors(context.getDisplayName());
  }
}
