package com.codeborne.selenide.junit5;

import com.codeborne.selenide.logevents.SimpleReport;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Aliaksandr Rasolka
 * @since 1.0.0
 */
@ParametersAreNonnullByDefault
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
   * @param onFailedTest parameter that indicate if need to log failed tests
   *
   * @return current extension instance
   */
  @Nonnull
  public TextReportExtension onFailedTest(final boolean onFailedTest) {
    this.onFailedTest = onFailedTest;
    return this;
  }

  /**
   * Initialize text report extension with specified successful tests log strategy.
   *
   * @param onSucceededTest parameter that indicate if need to log successful tests
   *
   * @return current extension instance
   */
  @Nonnull
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
    } else if (onSucceededTest) {
      report.finish(context.getDisplayName());
    }
  }

  @Override
  public void afterAll(final ExtensionContext context) {
    report.clean();
  }
}
