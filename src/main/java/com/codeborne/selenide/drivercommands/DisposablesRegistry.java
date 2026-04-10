package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.Disposable;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.currentThread;

/**
 * Singleton.
 * Holds all opened webdrivers in order to close them in the end.
 */
class DisposablesRegistry<K, T extends Disposable> {
  private static final Logger log = LoggerFactory.getLogger(DisposablesRegistry.class);

  @Nullable
  private SelenideCleanupShutdownHook shutdownHook;
  private final Map<K, T> disposables = new ConcurrentHashMap<>();

  public synchronized void register(K key, T disposable) {
    disposables.put(key, disposable);
    log.debug("Register {}:{} [size={}]", key, disposable, disposables.size());
    if (shutdownHook == null) {
      log.debug("Add shutdown hook in {} [size={}]", currentThread().getId(), disposables.size());
      shutdownHook = new SelenideCleanupShutdownHook();
      Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
  }

  public synchronized void unregister(K key) {
    T removed = disposables.remove(key);
    log.debug("Unregister {}:{} [size={}]", key, removed, disposables.size());
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
    disposables.values().forEach(Disposable::dispose);
  }

  public Optional<T> get(K key) {
    return Optional.ofNullable(disposables.get(key));
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
