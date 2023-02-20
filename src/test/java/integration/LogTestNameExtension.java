package integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;

import static integration.BaseIntegrationTest.browser;
import static org.slf4j.LoggerFactory.getLogger;

final class LogTestNameExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    getLogger(context.getDisplayName()).info("Starting tests @ {} ({})", browser, memory());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    getLogger(context.getDisplayName()).info("Finished tests @ {} - {} ({})", browser, verdict(context), memory());
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    getLogger(context.getRequiredTestClass().getName()).info("starting {} ({})...", context.getDisplayName(), memory());
  }

  @Override
  public void afterEach(ExtensionContext context) {
    getLogger(context.getRequiredTestClass().getName())
      .info("finished {} - {} ({})", context.getDisplayName(), verdict(context), memory());
  }

  private String verdict(ExtensionContext context) {
    return context.getExecutionException().isPresent() ?
      (context.getExecutionException().get() instanceof TestAbortedException ? "skipped" : "NOK") :
      "OK";
  }

  private String memory() {
    long freeMemory = Runtime.getRuntime().freeMemory();
    long maxMemory = Runtime.getRuntime().maxMemory();
    long totalMemory = Runtime.getRuntime().totalMemory();
    long usedMemory = totalMemory - freeMemory;
    return "memory used:" + mb(usedMemory) + ", free:" + mb(freeMemory) + ", total:" + mb(totalMemory) + ", max:" + mb(maxMemory);
  }

  private long mb(long bytes) {
    return bytes / 1024 / 1024;
  }
}
