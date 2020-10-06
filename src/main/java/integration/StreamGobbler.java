package integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

class StreamGobbler implements Runnable {
  private final InputStream inputStream;
  private final String filter;
  private final StringBuilder result = new StringBuilder();

  StreamGobbler(InputStream inputStream, String filter) {
    this.inputStream = inputStream;
    this.filter = filter;
  }

  @Override
  public void run() {
    new BufferedReader(new InputStreamReader(inputStream, UTF_8))
      .lines()
      .filter(line -> line.contains(filter))
      .forEach(line -> result.append(line).append('\n'));
  }

  public String getOutput() {
    return result.toString();
  }
}
