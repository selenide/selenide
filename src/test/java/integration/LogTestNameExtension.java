package integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static integration.BaseIntegrationTest.browser;
import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.fail;

class LogTestNameExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
  private static final Logger log = LoggerFactory.getLogger(LogTestNameExtension.class);

  private static int previousChromedriversCount = 0;

  @Override
  public void beforeAll(ExtensionContext context) {
    log.info("Starting {} {} @ {}", prefix(context), context.getDisplayName(), browser);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    log.info("Finished {} {} @ {} - {}", prefix(context), context.getDisplayName(), browser, verdict(context));
  }

  @Nonnull
  private String prefix(ExtensionContext context) {
    return context.getTestClass().map(klass ->
      ITest.class.isAssignableFrom(klass) ? "ITest" : "IntegrationTest").orElse("?Test");
  }

  private void assureNotTooManyOpenedBrowsers(ExtensionContext context) {
    int chromedrivers = CountChromeProcesses.count();
    if (chromedrivers > 2) {
      fail("***** Opened chromedrivers count " + chromedrivers + " is > 2 " +
        "[thread:" + currentThread().getId() + ":" + currentThread().getName() + "]");
    } else if (chromedrivers != previousChromedriversCount) {
      log.warn("***** Opened browsers count changed from {} to {} [thread:{}:{}] in {}", previousChromedriversCount,
        chromedrivers, currentThread().getId(), currentThread().getName(), context.getDisplayName());
    }
    previousChromedriversCount = chromedrivers;
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    log.info("  starting {} {}.{} (opened browsers: {})...",
      prefix(context), getTestClass(context), context.getDisplayName(), previousChromedriversCount);
    assureNotTooManyOpenedBrowsers(context);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    log.info("  finished {} {}.{} - {}...",
      prefix(context), getTestClass(context), context.getDisplayName(), verdict(context));
    assureNotTooManyOpenedBrowsers(context);
    log.info("  finished {} {} - {}.",
      prefix(context), context.getDisplayName(), verdict(context));
  }

  private String getTestClass(ExtensionContext context) {
    return context.getTestClass().map(Class::getName).orElse(null);
  }

  private String verdict(ExtensionContext context) {
    return context.getExecutionException().isPresent() ?
      (context.getExecutionException().get() instanceof TestAbortedException ? "skipped" : "NOK") :
      "OK";
  }
}
