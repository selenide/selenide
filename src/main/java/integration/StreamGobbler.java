package integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamGobbler implements Runnable {
  private final InputStream inputStream;
  private final StringBuilder consumer = new StringBuilder();

  public StreamGobbler(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public void run() {
    new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(line -> consumer.append(line).append('\n'));
  }

  public String getOutput() {
    return consumer.toString();
  }
}
