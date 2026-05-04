package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.impl.Disposable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class DisposablesRegistryTest {
  private final DisposablesRegistry<Integer, CovidTest> registry = new DisposablesRegistry<>();
  private final AtomicInteger created = new AtomicInteger(0);
  private final AtomicInteger disposed = new AtomicInteger(0);

  @AfterEach
  void tearDown() {
    registry.cancel();
  }

  @Test
  void firstRegister_startsShutdownHook() {
    assertThat(registry.isShutdownHookRegistered()).isFalse();
    CovidTest covidTest = new CovidTest();
    registry.register(covidTest.id, covidTest);
    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(1);
  }

  @Test
  void canUnregisteringItems_ifDisposedEarlier() {
    CovidTest test1 = new CovidTest();
    CovidTest test2 = new CovidTest();
    registry.register(test1.id, test1);
    registry.register(test2.id, test2);
    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(2);
    registry.unregister(test1.id);

    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(1);

    registry.unregister(test2.id);
    assertThat(registry.isShutdownHookRegistered()).isTrue();
    assertThat(registry.size()).isEqualTo(0);
  }

  @Test
  void shutdownHookDisposesAllLeftItems() {
    CovidTest test1 = new CovidTest();
    CovidTest test2 = new CovidTest();
    registry.register(test1.id, test1);
    registry.register(test2.id, test2);
    assertThat(created.get()).isEqualTo(2);
    assertThat(disposed.get()).isEqualTo(0);

    registry.disposeAllItems();

    assertThat(disposed.get()).isEqualTo(2);
  }

  @Test
  void overwritingKey_keepsOrphanForShutdownDisposal() {
    CovidTest test1 = new CovidTest();
    CovidTest test2 = new CovidTest();
    registry.register(99, test1);
    registry.register(99, test2);

    assertThat(registry.size()).isEqualTo(2);

    registry.disposeAllItems();

    assertThat(test1.disposed).isTrue();
    assertThat(test2.disposed).isTrue();
    assertThat(disposed.get()).isEqualTo(2);
  }

  @Test
  void unregister_removesItemFromShutdownDisposalSet() {
    CovidTest test = new CovidTest();
    registry.register(test.id, test);
    registry.unregister(test.id);

    assertThat(registry.size()).isEqualTo(0);

    registry.disposeAllItems();

    assertThat(test.disposed).isFalse();
    assertThat(disposed.get()).isEqualTo(0);
  }

  @Test
  void unregisterAfterOverwrite_disposesOnlyTheOrphan() {
    CovidTest orphan = new CovidTest();
    CovidTest current = new CovidTest();
    registry.register(99, orphan);
    registry.register(99, current);
    registry.unregister(99);

    assertThat(registry.size()).isEqualTo(1);

    registry.disposeAllItems();

    assertThat(orphan.disposed).isTrue();
    assertThat(current.disposed).isFalse();
    assertThat(disposed.get()).isEqualTo(1);
  }

  @Test
  void disposeAllItems_swallowsExceptions_andDisposesRemaining() {
    CovidTest before = new CovidTest();
    CovidTest broken = new CovidTest().failOnDispose();
    CovidTest after = new CovidTest();
    registry.register(before.id, before);
    registry.register(broken.id, broken);
    registry.register(after.id, after);

    registry.disposeAllItems();

    assertThat(before.disposed).isTrue();
    assertThat(after.disposed).isTrue();
  }

  private class CovidTest implements Disposable {
    private final int id;
    private boolean disposed;
    private boolean failOnDispose;

    CovidTest() {
      id = created.incrementAndGet();
    }

    CovidTest failOnDispose() {
      failOnDispose = true;
      return this;
    }

    @Override
    public void dispose() {
      if (failOnDispose) {
        throw new IllegalStateException("boom");
      }
      disposed = true;
      DisposablesRegistryTest.this.disposed.incrementAndGet();
    }
  }
}
