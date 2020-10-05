package integration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class CountChromeProcesses {
  private static final ExecutorService executorService1 = newSingleThreadExecutor();
  private static final ExecutorService executorService2 = newSingleThreadExecutor();

  public static int count() {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("pgrep", "-i", "chromedriver");
    builder.directory(new File(System.getProperty("user.home")));

    try {
      Process process = builder.start();
      StreamGobbler out = new StreamGobbler(process.getInputStream());
      StreamGobbler err = new StreamGobbler(process.getErrorStream());

      executorService1.submit(out);
      executorService2.submit(err);

      int exitCode = process.waitFor();
      executorService1.awaitTermination(100, MILLISECONDS);
      executorService2.awaitTermination(10, MILLISECONDS);

      if (isNotBlank(err.getOutput())) {
        throw new IllegalStateException("Failed to check opened browsers count: " + err.getOutput());
      }
      System.out.println("***** Exit code: " + exitCode + ", output: " + out.getOutput());
      return isBlank(out.getOutput()) ? 0 : out.getOutput().split("\n").length;
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Failed to check opened browsers count", e);
    }
  }
}
