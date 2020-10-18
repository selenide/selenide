package integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static integration.BaseIntegrationTest.browser;

final class LogTestNameExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    logger(context).info("Starting {} @ {}", context.getDisplayName(), browser);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    logger(context).info("Finished {} @ {} - {}", context.getDisplayName(), browser, verdict(context));
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    logger(context).info("  starting {} ...", context.getDisplayName());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    logger(context).info("  finished {} - {}", context.getDisplayName(), verdict(context));
  }

  private Logger logger(ExtensionContext context) {
    return LoggerFactory.getLogger(context.getTestClass().orElse(LogTestNameExtension.class));
  }

  private String verdict(ExtensionContext context) {
    return context.getExecutionException().isPresent() ?
      (context.getExecutionException().get() instanceof TestAbortedException ? "skipped" : "NOK") :
      "OK";
  }
}
