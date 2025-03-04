package com.codeborne.selenide.logevents;

import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.codeborne.selenide.logevents.ArgumentsPrinter.readableArguments;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 */
public class SelenideLogger {
  private static final Logger LOG = LoggerFactory.getLogger(SelenideLogger.class);
  private static final ThreadLocal<@Nullable Map<String, LogEventListener>> listeners = new ThreadLocal<>();
  private static final Pattern REGEX_UPPER_CASE = Pattern.compile("([A-Z])");
  private static final Map<String, LogEventListener> NO_LISTENERS = emptyMap();

  /**
   * Add a listener (to the current thread).
   *
   * @param name     unique name of this listener (per thread).
   *                 Can be used later to remove listener using method {@link #removeListener(String)}
   * @param listener event listener
   */
  public static void addListener(String name, LogEventListener listener) {
    Map<String, LogEventListener> threadListeners = listeners.get();
    if (threadListeners == null) {
      threadListeners = new HashMap<>();
    }

    threadListeners.put(name, listener);
    listeners.set(threadListeners);
    LOG.debug("Added listener '{}' to thread '{}' (now it has {} listeners)", name, currentThread().getId(), threadListeners.size());
  }

  public static SelenideLog beginStep(String source, String methodName, Object @Nullable... args) {
    return beginStep(source, getReadableSubject(methodName, args));
  }

  public static String getReadableSubject(String methodName, Object @Nullable... args) {
    return readableMethodName(methodName) + "(" + readableArguments(args) + ")";
  }

  static String readableMethodName(String methodName) {
    return REGEX_UPPER_CASE.matcher(methodName).replaceAll(" $1").toLowerCase(ROOT);
  }

  public static SelenideLog beginStep(String source, String subject) {
    Collection<LogEventListener> threadListeners = getEventLoggerListeners();

    SelenideLog log = new SelenideLog(source, subject);
    for (LogEventListener listener : threadListeners) {
      try {
        listener.beforeEvent(log);
      }
      catch (RuntimeException e) {
        LOG.error("Failed to call listener {}", listener, e);
      }
    }
    return log;
  }

  public static void commitStep(SelenideLog log, Throwable error) {
    log.setError(error);
    commitStep(log, FAIL);
  }

  public static void commitStep(SelenideLog log, EventStatus status) {
    log.setStatus(status);

    Collection<LogEventListener> threadListeners = getEventLoggerListeners();
    for (LogEventListener listener : threadListeners) {
      try {
        listener.afterEvent(log);
      }
      catch (RuntimeException e) {
        LOG.error("Failed to call listener {}", listener, e);
      }
    }
  }

  public static void run(String source, String subject, Runnable runnable) {
    wrap(source, subject, () -> {
      runnable.run();

      return null;
    });
  }

  public static <T> T get(String source, String subject, Supplier<T> supplier) {
    return requireNonNull(wrap(source, subject, supplier));
  }

  @Nullable
  @CanIgnoreReturnValue
  public static <T> T step(String source, Supplier<T> supplier) {
    return wrap(source, "", supplier);
  }

  public static void step(String source, Runnable runnable) {
    wrap(source, "", () -> {
      runnable.run();

      return null;
    });
  }

  @Nullable
  @CanIgnoreReturnValue
  private static <T> T wrap(String source, String subject, Supplier<@Nullable T> supplier) {
    SelenideLog log = SelenideLogger.beginStep(source, subject);
    try {
      T result = supplier.get();
      SelenideLogger.commitStep(log, PASS);
      return result;
    }
    catch (RuntimeException | Error e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  private static Collection<LogEventListener> getEventLoggerListeners() {
    return requireNonNullElse(listeners.get(), NO_LISTENERS).values();
  }

  /**
   * Remove listener (from the current thread).
   *
   * @param name unique name of listener added by method {@link #addListener(String, LogEventListener)}
   * @param <T>  class of listener to be returned
   * @return the listener being removed
   */
  @SuppressWarnings("unchecked")
  @Nullable
  @CanIgnoreReturnValue
  public static <T extends LogEventListener> T removeListener(String name) {
    Map<String, LogEventListener> threadListeners = SelenideLogger.listeners.get();
    if (threadListeners == null) {
      LOG.debug("Cannot remove listener '{}' for thread {} because no such listeners were registered", name, currentThread().getId());
      return null;
    }
    T listener = (T) threadListeners.remove(name);
    LOG.debug("Removed listener '{}' for thread '{}' (now it has {} listeners)", name, currentThread().getId(), threadListeners.size());
    return listener;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  static <T extends LogEventListener> T getListener(String name) {
    Map<String, LogEventListener> threadListeners = SelenideLogger.listeners.get();
    if (threadListeners == null) {
      LOG.warn("No listeners found for thread '{}'", currentThread().getId());
      return null;
    }
    T listener = (T) threadListeners.get(name);
    if (listener == null) {
      LOG.warn("No listener '{}' found for thread '{}' (it has {} listeners)", name, currentThread().getId(), threadListeners.size());
    }

    LOG.debug("Found listener '{}' for thread '{}': {} (it has {} listeners)",
      name, currentThread().getId(), listener, threadListeners.size());
    return listener;
  }

  /**
   * Remove all listeners FOR CURRENT THREAD
   */
  public static void removeAllListeners() {
    Map<String, LogEventListener> threadListeners = requireNonNullElse(listeners.get(), NO_LISTENERS);
    LOG.debug("Removing {} listeners from thread '{}'", threadListeners.size(), currentThread().getId());
    listeners.remove();
  }

  /**
   * If listener with given name is bound (added) to the current thread.
   *
   * @param name unique name of listener added by method {@link #addListener(String, LogEventListener)}
   * @return true if method {@link #addListener(String, LogEventListener)} with
   * corresponding name has been called in current thread.
   */
  public static boolean hasListener(String name) {
    Map<String, LogEventListener> threadListeners = SelenideLogger.listeners.get();
    return threadListeners != null && threadListeners.containsKey(name);
  }
}
