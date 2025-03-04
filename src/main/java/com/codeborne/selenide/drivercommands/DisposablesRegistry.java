package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.Disposable;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static java.lang.Thread.currentThread;

/**
 * Singleton.
 * Holds all opened webdrivers in order to close them in the end.
 */
class DisposablesRegistry<T extends Disposable> {
  private static final Logger log = LoggerFactory.getLogger(DisposablesRegistry.class);

  @Nullable
  private SelenideCleanupShutdownHook shutdownHook;
  private final List<T> disposables = new CopyOnWriteArrayList<>();

  public synchronized void register(T disposable) {
    disposables.add(disposable);
    log.debug("Register {} in {} [size={}]", disposable, currentThread().getId(), disposables.size());
    if (shutdownHook == null) {
      log.debug("Add shutdown hook in {} [size={}]", currentThread().getId(), disposables.size());
      shutdownHook = new SelenideCleanupShutdownHook();
      Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
  }

  public synchronized void unregister(T webdriver) {
    disposables.remove(webdriver);
    log.debug("Unregister {} in {} [size={}]", webdriver, currentThread().getId(), disposables.size());
  }

  synchronized void cancel() {
    if (isShutdownHookRegistered()) {
      log.debug("Remove shutdown hook in {}", currentThread().getId());
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

  public Optional<T> find(Predicate<T> filter) {
    return disposables.stream().filter(filter).findFirst();
  }

  private class SelenideCleanupShutdownHook extends Thread {
    @Override
    public void run() {
      log.debug("Run cleanup: size={}", disposables.size());
      disposeAllItems();
      log.debug("Finished cleanup");
    }
  }
}
