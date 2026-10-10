package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static com.codeborne.selenide.logevents.SelenideLogger.numberOfThreadsWithListeners;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Isolated("counts threads having listeners in static SelenideLogger")
final class SelenideLoggerThreadsTest {
  private final LogEventListener listener = mock();

  @BeforeEach
  void setUp() {
    SelenideLogger.removeAllListeners();
  }

  @AfterEach
  void tearDown() {
    SelenideLogger.removeAllListeners();
  }

  @Test
  void doesNotUseThreadLocal_ifNoThreadHasListeners() throws InterruptedException {
    assertThat(numberOfThreadsWithListeners())
      .as("threads with listeners - does another test add listeners without removing them?")
      .isZero();

    boolean usedThreadLocal = usesThreadLocal(() -> {
      SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click"), PASS);
      SelenideLogger.hasListener("listener");
      SelenideLogger.removeListener("listener");
      SelenideLogger.removeAllListeners();
    });

    assertThat(usedThreadLocal).as("thread has an entry of SelenideLogger's ThreadLocal").isFalse();
  }

  @Test
  void usesThreadLocal_ifAnyThreadHasListeners() throws InterruptedException {
    SelenideLogger.addListener("listener", listener);

    boolean usedThreadLocal = usesThreadLocal(() -> SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click"), PASS));

    assertThat(usedThreadLocal).as("thread has an entry of SelenideLogger's ThreadLocal").isTrue();
  }

  @Test
  void removeAllListeners_removesEmptyThreadLocalEntry_ifAnotherThreadHasListeners() throws InterruptedException {
    SelenideLogger.addListener("listener", listener);

    boolean usedThreadLocal = usesThreadLocal(() -> {
      SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click"), PASS);
      SelenideLogger.removeAllListeners();
    });

    assertThat(usedThreadLocal).as("thread has an entry of SelenideLogger's ThreadLocal").isFalse();
  }

  @Test
  void countsThreadUntilItsLastListenerIsRemoved() {
    int initialThreadsWithListeners = numberOfThreadsWithListeners();
    SelenideLogger.addListener("first", listener);
    SelenideLogger.addListener("second", listener);
    assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners + 1);

    SelenideLogger.removeListener("first");
    assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners + 1);
    assertThat(SelenideLogger.hasListener("second")).isTrue();

    SelenideLogger.removeListener("second");
    assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners);
    assertThat(SelenideLogger.hasListener("second")).isFalse();
  }

  @Test
  void removeAllListeners_stopsCountingThread() {
    int initialThreadsWithListeners = numberOfThreadsWithListeners();
    SelenideLogger.addListener("first", listener);
    SelenideLogger.addListener("second", listener);

    SelenideLogger.removeAllListeners();

    assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners);
    assertThat(SelenideLogger.hasListener("first")).isFalse();
  }

  @Test
  void removingListeners_withoutListeners_doesNothing() {
    int initialThreadsWithListeners = numberOfThreadsWithListeners();
    assertThat(SelenideLogger.<LogEventListener>removeListener("unknown")).isNull();
    SelenideLogger.removeAllListeners();

    assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners);
  }

  @Test
  void countsListenersOfOtherThreads() throws InterruptedException {
    int initialThreadsWithListeners = numberOfThreadsWithListeners();
    CountDownLatch added = new CountDownLatch(1);
    CountDownLatch checked = new CountDownLatch(1);
    Thread otherThread = new Thread(() -> {
      SelenideLogger.addListener("other", listener);
      added.countDown();
      awaitQuietly(checked);
      SelenideLogger.removeAllListeners();
    });
    otherThread.start();
    try {
      assertThat(added.await(10, SECONDS)).as("other thread added its listener").isTrue();
      assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners + 1);
      assertThat(SelenideLogger.hasListener("other")).isFalse();
    }
    finally {
      checked.countDown();
      otherThread.join(SECONDS.toMillis(10));
    }
    assertThat(otherThread.isAlive()).as("other thread is still running").isFalse();
    assertThat(numberOfThreadsWithListeners()).isEqualTo(initialThreadsWithListeners);
  }

  @Test
  void notifiesListener_addedAfterAllListenersWereRemoved() {
    SelenideLogger.addListener("listener", mock());
    SelenideLogger.removeAllListeners();
    SelenideLogger.addListener("listener", listener);

    SelenideLogger.commitStep(SelenideLogger.beginStep("div", "click"), PASS);

    verify(listener).beforeEvent(any());
    verify(listener).afterEvent(any());
  }

  /**
   * Runs the action in a new thread and checks if the thread has an entry of SelenideLogger's ThreadLocal afterwards.
   */
  private static boolean usesThreadLocal(Runnable action) throws InterruptedException {
    AtomicReference<Boolean> result = new AtomicReference<>();
    Thread thread = new Thread(() -> {
      action.run();
      result.set(hasEntry(Thread.currentThread(), field(SelenideLogger.class, "listeners", null)));
    });
    thread.start();
    thread.join();
    assertThat(result.get()).as("result of the thread").isNotNull();
    return result.get();
  }

  private static boolean hasEntry(Thread thread, Object threadLocal) {
    Object map = field(Thread.class, "threadLocals", thread);
    if (map == null) {
      return false;
    }
    for (Object entry : (Object[]) field(map.getClass(), "table", map)) {
      if (entry != null && ((Reference<?>) entry).get() == threadLocal) {
        return true;
      }
    }
    return false;
  }

  private static Object field(Class<?> type, String name, Object instance) {
    try {
      Field field = type.getDeclaredField(name);
      field.setAccessible(true);
      return field.get(instance);
    }
    catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Cannot read " + type.getName() + "." + name, e);
    }
  }

  private static void awaitQuietly(CountDownLatch latch) {
    try {
      latch.await(10, SECONDS);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
