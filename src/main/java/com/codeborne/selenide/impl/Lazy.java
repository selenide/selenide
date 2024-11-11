package com.codeborne.selenide.impl;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class Lazy<T> {
  @Nullable
  private volatile T value;
  private final Supplier<T> supplier;

  private Lazy(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    if (value == null) {
      synchronized (supplier) {
        if (value == null) {
          value = supplier.get();
        }
      }
    }
    return requireNonNull(value);
  }

  public static <T> Lazy<T> lazyEvaluated(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }
}
