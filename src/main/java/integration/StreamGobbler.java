package integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

class StreamGobbler implements Runnable {
  private final InputStream inputStream;
  private final StringBuilder consumer = new StringBuilder();

  StreamGobbler(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public void run() {
    new BufferedReader(new InputStreamReader(inputStream, UTF_8))
      .lines()
      .forEach(line -> consumer.append(line).append('\n'));
  }

  public String getOutput() {
    return consumer.toString();
  }
}
