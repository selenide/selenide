package com.codeborne.selenide.junit5;

import com.codeborne.selenide.logevents.SimpleReport;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Aliaksandr Rasolka
 */
public class TextReportExtension implements BeforeEachCallback, AfterEachCallback, AfterAllCallback {
  private final SimpleReport report;
  private boolean onFailedTest;
  private boolean onSucceededTest;

  /**
   * Initialize default text report extension that log successful and failed tests.
   */
  public TextReportExtension() {
    this.report = new SimpleReport();
    this.onFailedTest = true;
    this.onSucceededTest = true;
  }

  /**
   * Initialize text report extension with specified failed tests log strategy.
   *
   * @param onFailedTest whether to log failed tests
   * @return current extension instance
   */
  @CanIgnoreReturnValue
  public TextReportExtension onFailedTest(final boolean onFailedTest) {
    this.onFailedTest = onFailedTest;
    return this;
  }

  /**
   * Initialize text report extension with specified successful tests log strategy.
   *
   * @param onSucceededTest whether to log succeeded tests
   * @return current extension instance
   */
  @CanIgnoreReturnValue
  public TextReportExtension onSucceededTest(final boolean onSucceededTest) {
    this.onSucceededTest = onSucceededTest;
    return this;
  }

  @Override
  public void beforeEach(final ExtensionContext context) {
    if (onFailedTest || onSucceededTest) {
      report.start();
    }
  }

  @Override
  public void afterEach(final ExtensionContext context) {
    if (onFailedTest && context.getExecutionException().isPresent()) {
      report.finish(context.getDisplayName());
    }
    else if (onSucceededTest) {
      String uniqueId = context.getUniqueId();
      report.finish(uniqueId);
    }
  }

  @Override
  public void afterAll(final ExtensionContext context) {
    report.clean();
  }
}
