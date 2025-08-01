package integration.videorecorder.core;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;

import static com.codeborne.selenide.Configuration.reportsFolder;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;

public class ThreadDumper {
  private final ScheduledExecutorService executor = newScheduledThreadPool(1);
  private final String videoId;

  public ThreadDumper(String videoId) {
    this.videoId = videoId;
  }

  void start() {
    executor.scheduleWithFixedDelay(this::takeThreadDump, 0, 500, MILLISECONDS);
  }

  private void takeThreadDump() {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    ThreadInfo[] infos = bean.dumpAllThreads(true, true);
    String dump = Arrays.stream(infos).map(Object::toString).collect(joining("\n"));
    try {
      File dir = new File(reportsFolder, "%s.dumps".formatted(videoId));
      dir.mkdirs();
      FileUtils.writeStringToFile(new File(dir, "%d.dump".formatted(currentTimeMillis())), dump, UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void stop() throws InterruptedException {
    executor.shutdown();
    executor.awaitTermination(2, SECONDS);
    executor.shutdownNow();
  }
}
