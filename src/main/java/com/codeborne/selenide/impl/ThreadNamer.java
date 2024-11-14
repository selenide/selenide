package com.codeborne.selenide.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadNamer implements ThreadFactory {
  private static final AtomicLong counter = new AtomicLong();
  private final String name;

  public static ThreadNamer named(String name) {
    return new ThreadNamer(name);
  }

  private ThreadNamer(String name) {
    this.name = name;
  }

  @Override
  public Thread newThread(Runnable runnable) {
    return new Thread(runnable, name + counter.incrementAndGet());
  }
}
