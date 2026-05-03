# DisposablesRegistry: prevent webdriver leak on overwrite

## Problem

`DisposablesRegistry<K, T extends Disposable>` (`src/main/java/com/codeborne/selenide/drivercommands/DisposablesRegistry.java`) holds disposables in a `Map<K, T>` keyed by, in practice, the thread ID. The Map was introduced in commit `8eda33eb3` to give the screenshooter O(1) lookup of the current thread's webdriver.

When `register(key, newDriver)` is called and `key` already has an entry, the `Map.put` silently overwrites the previous entry. The previous `WebDriverInstance` becomes unreachable through the registry and is never disposed — neither during normal flow nor at JVM shutdown. The `log.warn("Unclosed webdriver detected ...")` added to `register` reports the situation but does not fix the leak.

## Goal

Guarantee that every `Disposable` ever registered is disposed at the latest by JVM shutdown, even when a caller registers a new disposable under an existing key without first calling `unregister` (i.e., a "defensive safety net" — the registry is the last line of defense, not a tool for diagnosing buggy callers).

The fast O(1) lookup behavior (`get(K)`) must be preserved.

## Non-goals

- Diagnosing or fixing the upstream callers that double-register. The local diff already adds a `source` (thread name) field to `WebDriverInstance` for diagnostic logging; that work is complementary and stays outside this refactor.
- Eager disposal of an orphan at the moment a double-register is detected. We accept that an orphaned browser process may live until JVM shutdown.
- Making `Disposable.dispose()` idempotent. `disposeAllItems()` will swallow exceptions (see below) so a misbehaving disposable cannot block the rest, but `dispose()` itself is not changed.

## Design

### Data structure

```java
class DisposablesRegistry<K, T extends Disposable> {
  private final Map<K, T> byKey = new ConcurrentHashMap<>();
  private final List<T> all = new ArrayList<>();
  // shutdownHook unchanged
}
```

- `byKey` keeps O(1) lookup by key (preserves the optimization from `8eda33eb3`).
- `all` is the authoritative list of every disposable the registry has promised to dispose. It is the single source of truth used by `disposeAllItems()`.

`ArrayList` is acceptable because all mutations happen inside the existing `synchronized` methods (`register`, `unregister`, `cancel`). No additional concurrency primitives are needed.

### Behavior

**`register(key, disposable)`** (synchronized)
1. If `byKey.get(key)` returns a non-null `previous`: keep the existing `log.warn("Unclosed webdriver detected ...")`. Do **not** remove `previous` from `all` — leaving it in `all` is what makes the shutdown hook eventually dispose it.
2. `byKey.put(key, disposable)`
3. `all.add(disposable)`
4. Existing shutdown-hook-installation logic stays unchanged.

**`unregister(key)`** (synchronized)
1. `T removed = byKey.remove(key)`
2. If `removed != null`: `all.remove(removed)`. `unregister` is the caller's signal that they are taking ownership and have disposed (or will dispose) it themselves; we don't want to dispose it again at shutdown.

   *Implication:* an entry that was overwritten by `register` and never unregistered is never removed from `all`, so the shutdown hook always sees it.

**`disposeAllItems()`**
1. Iterate `all` and call `dispose()` on each.
2. Swallow exceptions per-element (log at `warn` level) so a single broken disposable cannot block the rest of the cleanup.

   ```java
   for (T item : all) {
     try {
       item.dispose();
     } catch (RuntimeException e) {
       log.warn("Failed to dispose {}", item, e);
     }
   }
   ```

**`size()`**
- Returns `all.size()` (count of undisposed instances, including orphans). This makes the test assertions and the existing `[size=...]` log lines reflect the true number of disposables the registry is still responsible for.

**`get(K key)`, `cancel()`, `isShutdownHookRegistered()`**
- Unchanged.

### Public API impact

`WebdriversRegistry` (the only production caller) is unchanged. The leak-fix itself does not require changes to `WebdriversRegistryTest`; the test is, however, modified in the same working tree by the parallel `WebDriverInstance.source` diagnostic work (constructor signature gained an argument), which is out of scope for this design.

### Concurrency

`register`, `unregister`, `cancel` remain `synchronized` on the registry instance, which protects all mutations to both `byKey` and `all`. `get(K)` reads `byKey` without locking — same as today. `disposeAllItems()` will also be `synchronized` on the registry instance: shutdown can race with a thread that is still mid-test, so iterating `all` under the monitor avoids `ConcurrentModificationException` and ensures we see a consistent snapshot.

## Tests

Add to `src/test/java/com/codeborne/selenide/drivercommands/DisposablesRegistryTest.java`:

1. **Overwrite preserves orphan for shutdown.** Register `a` under key `1`. Register `b` under key `1` (no `unregister` between). Call `disposeAllItems()`. Both `a` and `b` are disposed.
2. **Unregister removes from shutdown set.** Register `a` under key `1`. Unregister key `1`. Call `disposeAllItems()`. `a` is **not** disposed.
3. **Mixed orphan + clean unregister.** Register `a` under key `1`. Register `b` under key `1` (overwrites). Unregister key `1` (removes `b`). Call `disposeAllItems()`. Only `a` is disposed.
4. **Exception in dispose does not block others.** Register `a`, `b`, `c` (different keys). `b.dispose()` throws. Call `disposeAllItems()`. `a` and `c` are still disposed.
5. **`size()` reflects `all`, not `byKey`.** After registering `a`, registering `b` under the same key, and unregistering one of them, `size()` returns the expected count of still-tracked disposables.

`WebdriversRegistryTest` does not need new assertions for this design — public behavior of `WebdriversRegistry` is unchanged.

## Files touched

- `src/main/java/com/codeborne/selenide/drivercommands/DisposablesRegistry.java` — implementation change.
- `src/test/java/com/codeborne/selenide/drivercommands/DisposablesRegistryTest.java` — new test cases.

No changes to `WebdriversRegistry`, `WebDriverInstance`, or any caller from the leak-fix itself. (Other files in the working tree — `WebDriverInstance.java`, `WebdriversRegistryTest.java`, several integration tests — are modified by the parallel `source` diagnostic work, which is out of scope here.)
