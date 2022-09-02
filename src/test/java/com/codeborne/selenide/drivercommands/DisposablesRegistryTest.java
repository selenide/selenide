package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.Disposable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class DisposablesRegistryTest {
  private final DisposablesRegistry<CovidTest> registry = new DisposablesRegistry<>();
  private final AtomicInteger created = new AtomicInteger(0);
  private final AtomicInteger disposed = new AtomicInteger(0);

  @AfterEach
  void tearDown() {
    registry.cancel();
  }

  @Test
  void firstRegister_startsShutdownHook() {
    assertThat(registry.isShutdownHookRegistered()).isFalse();
    registry.register(new CovidTest());
    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(1);
  }

  @Test
  void canUnregisteringItems_ifDisposedEarlier() {
    CovidTest test1 = new CovidTest();
    CovidTest test2 = new CovidTest();
    registry.register(test1);
    registry.register(test2);
    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(2);
    registry.unregister(test1);

    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(1);

    registry.unregister(test2);
    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(0);
  }

  @Test
  void shutdownHookDisposesAllLeftItems() {
    CovidTest test1 = new CovidTest();
    CovidTest test2 = new CovidTest();
    registry.register(test1);
    registry.register(test2);
    assertThat(created.get()).isEqualTo(2);
    assertThat(disposed.get()).isEqualTo(0);

    registry.disposeAllItems();

    assertThat(disposed.get()).isEqualTo(2);
  }

  private class CovidTest implements Disposable {
    CovidTest() {
      created.incrementAndGet();
    }

    @Override
    public void dispose() {
      disposed.incrementAndGet();
    }
  }
}
