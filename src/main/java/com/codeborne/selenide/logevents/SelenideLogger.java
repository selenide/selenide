package com.codeborne.selenide.logevents;

import com.codeborne.selenide.impl.DurationFormat;
import com.codeborne.selenide.logevents.LogEvent.EventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.FAIL;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;

/**
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 */
@ParametersAreNonnullByDefault
public class SelenideLogger {
  private static final Logger LOG = LoggerFactory.getLogger(SelenideLogger.class);

  protected static final ThreadLocal<Map<String, LogEventListener>> listeners = new ThreadLocal<>();

  private static final DurationFormat df = new DurationFormat();
  private static final Pattern REGEX_UPPER_CASE = Pattern.compile("([A-Z])");

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
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideLog beginStep(String source, String methodName, @Nullable Object... args) {
    return beginStep(source, getReadableSubject(methodName, args));
  }

  @CheckReturnValue
  @Nonnull
  public static String getReadableSubject(String methodName, @Nullable Object... args) {
    return readableMethodName(methodName) + "(" + readableArguments(args) + ")";
  }

  @CheckReturnValue
  @Nonnull
  static String readableMethodName(String methodName) {
    return REGEX_UPPER_CASE.matcher(methodName).replaceAll(" $1").toLowerCase(ROOT);
  }

  @CheckReturnValue
  @Nonnull
  static String readableArguments(@Nullable Object... args) {
    if (args == null || args.length == 0) {
      return "";
    }

    if (args[0] instanceof Object[]) {
      return arrayToString((Object[]) args[0]);
    }

    if (args[0] instanceof int[]) {
      return arrayToString((int[]) args[0]);
    }

    return arrayToString(args);
  }

  @CheckReturnValue
  @Nonnull
  private static String arrayToString(Object[] args) {
    return args.length == 1 ?
      argToString(args[0]) :
      '[' + Stream.of(args).map(SelenideLogger::argToString).collect(joining(", ")) + ']';
  }

  @CheckReturnValue
  @Nonnull
  private static String argToString(Object arg) {
    if (arg instanceof Duration) {
      return df.format((Duration) arg);
    }
    return String.valueOf(arg);
  }

  @CheckReturnValue
  @Nonnull
  private static String arrayToString(int[] args) {
    return args.length == 1 ? String.valueOf(args[0]) : Arrays.toString(args);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideLog beginStep(String source, String subject) {
    Collection<LogEventListener> listeners = getEventLoggerListeners();

    SelenideLog log = new SelenideLog(source, subject);
    for (LogEventListener listener : listeners) {
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

    Collection<LogEventListener> listeners = getEventLoggerListeners();
    for (LogEventListener listener : listeners) {
      try {
        listener.afterEvent(log);
      }
      catch (RuntimeException e) {
        LOG.error("Failed to call listener {}", listener, e);
      }
    }
  }

  public static void run(String source, String subject, Runnable runnable) {
    SelenideLog log = SelenideLogger.beginStep(source, subject);
    try {
      runnable.run();
      SelenideLogger.commitStep(log, PASS);
    }
    catch (RuntimeException | Error e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  @CheckReturnValue
  public static <T> T get(String source, String subject, Supplier<T> supplier) {
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

  @CheckReturnValue
  @Nonnull
  private static Collection<LogEventListener> getEventLoggerListeners() {
    if (listeners.get() == null) {
      listeners.set(new HashMap<>());
    }
    return listeners.get().values();
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
  public static <T extends LogEventListener> T removeListener(String name) {
    Map<String, LogEventListener> listeners = SelenideLogger.listeners.get();
    return listeners == null ? null : (T) listeners.remove(name);
  }

  public static void removeAllListeners() {
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
    Map<String, LogEventListener> listeners = SelenideLogger.listeners.get();
    return listeners != null && listeners.containsKey(name);
  }
}
