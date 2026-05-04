package integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.opentest4j.TestAbortedException;

import static integration.BaseIntegrationTest.browser;
import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNullElseGet;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.slf4j.LoggerFactory.getLogger;

public final class LogTestNameExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
  private static final Namespace namespace = create(LogTestNameExtension.class);
  private static final String CLASS_THREAD_NAME = "class-thread-name";
  private static final String METHOD_THREAD_NAME = "method-thread-name";

  @Override
  public void beforeAll(ExtensionContext context) {
    getLogger(context.getDisplayName()).info("Starting tests @ {} ({})", browser, memory());
    context.getStore(namespace).put(CLASS_THREAD_NAME, currentThread().getName());
    currentThread().setName("%s:%s".formatted(currentThread().getName(), context.getDisplayName()));
  }

  @Override
  public void afterAll(ExtensionContext context) {
    getLogger(context.getDisplayName()).info("Finished tests @ {} - {} ({})", browser, verdict(context), memory());

    restoreThreadName(context, CLASS_THREAD_NAME);
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    getLogger(context.getRequiredTestClass().getName()).info("starting {} ({})...", context.getDisplayName(), memory());
    Store store = context.getStore(namespace);
    store.put(METHOD_THREAD_NAME, currentThread().getName());
    currentThread().setName("%s:%s.%s".formatted(store.get(CLASS_THREAD_NAME),
      context.getRequiredTestClass().getName(), context.getDisplayName()));
  }

  @Override
  public void afterEach(ExtensionContext context) {
    getLogger(context.getRequiredTestClass().getName())
      .info("finished {} - {} ({})", context.getDisplayName(), verdict(context), memory());

    restoreThreadName(context, METHOD_THREAD_NAME);
  }

  private void restoreThreadName(ExtensionContext context, String name) {
    String originalThreadName = context.getStore(namespace).remove(name, String.class);
    currentThread().setName(requireNonNullElseGet(originalThreadName, () -> context.getDisplayName()));
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
