package integration;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static integration.BaseIntegrationTest.browser;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;

final class LogTestNameExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
  @Override
  public void beforeAll(ExtensionContext context) throws IOException, InterruptedException {
    getLogger(context.getDisplayName()).info("Starting tests @ {} {}, firefoxes:{}", browser, memory(), firefoxes());
  }

  @Override
  public void afterAll(ExtensionContext context) throws IOException, InterruptedException {
    getLogger(context.getDisplayName()).info("Finished tests @ {} - {} {}, firefoxes:{}", browser, verdict(context), memory(), firefoxes());
  }

  @Override
  public void beforeEach(ExtensionContext context) throws IOException, InterruptedException {
    getLogger(context.getRequiredTestClass().getName())
      .info("starting {} {}, firefoxes:{}...", context.getDisplayName(), memory(), firefoxes());
  }

  @Override
  public void afterEach(ExtensionContext context) throws IOException, InterruptedException {
    getLogger(context.getRequiredTestClass().getName())
      .info("finished {} - {} {}, firefoxes:{}", context.getDisplayName(), verdict(context), memory(), firefoxes());
  }

  private String verdict(ExtensionContext context) {
    return context.getExecutionException().isPresent() ?
      (context.getExecutionException().get() instanceof TestAbortedException ? "skipped" : "NOK") :
      "OK";
  }

  private String memory() {
    long freeMemory = Runtime.getRuntime().freeMemory();
    long maxMemory = Runtime.getRuntime().maxMemory();
    long totalMemory = Runtime.getRuntime().totalMemory();
    long usedMemoty = totalMemory - freeMemory;
    return "used:" + mb(usedMemoty) + ", free:" + mb(freeMemory) + ", total:" + mb(totalMemory) + ", max:" + mb(maxMemory);
  }

  private long mb(long bytes) {
    return bytes / 1024 / 1024;
  }

  private String firefoxes() throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec(new String[] {"ps", "aux"});
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
      writer.append("");
    }

    if (!process.waitFor(2, TimeUnit.SECONDS)) {
      System.out.println("Process not closed");
//      process.destroyForcibly();
//      process.waitFor(2, TimeUnit.SECONDS);
      return readOutput(process);
    }
    if (process.exitValue() == 0) {
      return readOutput(process);
    }
    else {
      String b = IOUtils.toString(process.getErrorStream(), UTF_8);
      return b;
    }
  }

  @Nonnull
  private String readOutput(Process process) throws IOException {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      List<String> a = in.lines().filter(this::isFirefox).collect(Collectors.toList());

      String s = "Firefoxes: " + a.size() + ":\n" + StringUtils.join(a, "\n");
      return s;
    }
  }

  private boolean isFirefox(String line) {
    String l = line.toLowerCase();
    return l.contains("firefox") && !l.contains("java");
  }
}
