package com.codeborne.selenide.integrationtests;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;

public class ParallelTest {
  public static final int THREADS = 3;
  public static final int EXECUTIONS = 5;

  private static final URL pageUrl = currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html");
  private static final AtomicInteger counter = new AtomicInteger();
  private static final AtomicInteger errors = new AtomicInteger();
  private static final AtomicInteger completed = new AtomicInteger();

  @Test @Ignore
  public void canRunMultipleWebDriversInParallelThreads() {
    for (int thread = 0; thread < THREADS; thread++) {
      new Thread(new WebDriverTestThread()).start();
    }
    while (completed.get() < THREADS) {
      sleep(100);
    }

    assertEquals(0, errors.get());
  }

  private class WebDriverTestThread implements Runnable {
    private final int threadId = counter.incrementAndGet();

    @Override
    public void run() {
      try {
        for (int i = 0; i < EXECUTIONS; i++) {
          System.out.println("Thread #" + threadId + ", execution #" + i);
          open(pageUrl + "?thread=" + threadId);
          $("#username").val("Thread#" + threadId);
          sleep((long) (500 * Math.random()));
          $("#username-mirror").shouldHave(text("Thread#" + threadId));
        }
      } catch (Exception e) {
        e.printStackTrace();
        errors.incrementAndGet();
      }

      completed.incrementAndGet();
    }
  }
}
