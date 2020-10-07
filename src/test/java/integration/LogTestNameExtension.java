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
  private static final Logger log = LoggerFactory.getLogger(LogTestNameExtension.class);

  @Override
  public void beforeAll(ExtensionContext context) {
    log.info("Starting {} @ {}", context.getDisplayName(), browser);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    log.info("Finished {} @ {} - {}", context.getDisplayName(), browser, verdict(context));
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    log.info("  starting {} ...", context.getDisplayName());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    log.info("  finished {} - {}", context.getDisplayName(), verdict(context));
  }

  private String verdict(ExtensionContext context) {
    return context.getExecutionException().isPresent() ?
      (context.getExecutionException().get() instanceof TestAbortedException ? "skipped" : "NOK") :
      "OK";
  }
}
