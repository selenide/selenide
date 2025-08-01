package org.selenide.videorecorder.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

final class ThreadPool extends ScheduledThreadPoolExecutor {
  ThreadPool(int corePoolSize, ThreadFactory threadFactory) {
    super(corePoolSize, threadFactory);
    setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
  }
}
