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
import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.fail;

class LogTestNameExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
  private static final Logger log = LoggerFactory.getLogger(LogTestNameExtension.class);

  private static int previousChromedriversCount = 0;

  @Override
  public void beforeAll(ExtensionContext context) {
    log.info("Starting {} @ {}", context.getDisplayName(), browser);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    log.info("Finished {} @ {} - {}", context.getDisplayName(), browser, verdict(context));
  }

  private void assureNotTooManyOpenedBrowsers(ExtensionContext context) {
    int chromedrivers = CountChromeProcesses.count();
    if (chromedrivers > 1) {
      fail("***** Opened chromedrivers count " + chromedrivers + " is > 1 " +
        "[thread:" + currentThread().getId() + ":" + currentThread().getName() + "]");
    } else if (chromedrivers != previousChromedriversCount) {
      log.warn("***** Opened browsers count changed from {} to {} [thread:{}:{}] in {}", previousChromedriversCount,
        chromedrivers, currentThread().getId(), currentThread().getName(), context.getDisplayName());
    }
    previousChromedriversCount = chromedrivers;
  }

  private void assureBrowserIsChrome() {
    if (!"chrome".equals(System.getProperty("selenide.browser"))) {
      fail("WTF! 'selenide.browser' has been changed to " + System.getProperty("selenide.browser"));
    }
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    log.info("  starting {} (opened browsers: {})...", context.getDisplayName(), previousChromedriversCount);
    assureBrowserIsChrome();
    assureNotTooManyOpenedBrowsers(context);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    log.info("  finished {} - {}...", context.getDisplayName(), verdict(context));
    assureBrowserIsChrome();
    assureNotTooManyOpenedBrowsers(context);
    log.info("  finished {} - {}.", context.getDisplayName(), verdict(context));
  }

  private String verdict(ExtensionContext context) {
    return context.getExecutionException().isPresent() ?
      (context.getExecutionException().get() instanceof TestAbortedException ? "skipped" : "NOK") :
      "OK";
  }
}
