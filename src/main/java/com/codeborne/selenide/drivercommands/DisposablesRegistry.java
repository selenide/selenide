package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.currentThread;

/**
 * Singleton.
 * Holds all opened webdrivers in order to close them in the end.
 *
 * @since 6.7.2
 */
@ParametersAreNonnullByDefault
class DisposablesRegistry<T extends Disposable> {
  private static final Logger log = LoggerFactory.getLogger(DisposablesRegistry.class);

  @Nullable
  private SelenideCleanupShutdownHook shutdownHook = null;
  private final List<T> disposables = new CopyOnWriteArrayList<>();

  public synchronized void register(T disposable) {
    disposables.add(disposable);
    log.info("Register {} in {} [size={}]", disposable, currentThread().getId(), disposables.size());
    if (shutdownHook == null) {
      log.info("Add shutdown hook in {} [size={}]", currentThread().getId(), disposables.size());
      shutdownHook = new SelenideCleanupShutdownHook();
      Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
  }

  public synchronized void unregister(T webdriver) {
    disposables.remove(webdriver);
    log.info("Unregister {} in {} [size={}]", webdriver, currentThread().getId(), disposables.size());
  }

  synchronized void cancel() {
    if (isShutdownHookRegistered()) {
      log.info("Remove shutdown hook in {}", currentThread().getId());
      Runtime.getRuntime().removeShutdownHook(shutdownHook);
      shutdownHook = null;
    }
  }

  int size() {
    return disposables.size();
  }

  synchronized boolean isShutdownHookRegistered() {
    return shutdownHook != null;
  }

  void disposeAllItems() {
    disposables.forEach(Disposable::dispose);
  }

  private class SelenideCleanupShutdownHook extends Thread {
    @Override
    public void run() {
      log.info("Run cleanup: size={}", disposables.size());
      disposeAllItems();
      log.info("Finished cleanup");
    }
  }
}
