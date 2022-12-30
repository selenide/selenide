package integration.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Thread.sleep;

class Delayer {
  private long remainingDelay;

  Delayer(long durationMs) {
    this.remainingDelay = durationMs;
  }

  void delay(long milliseconds) {
    if (remainingDelay > 0) {
      pause(Math.min(remainingDelay, milliseconds));
      remainingDelay -= milliseconds;
    }
  }

  static void pause(long milliseconds) {
    try {
      sleep(milliseconds);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  static void writeSlowly(OutputStream os, int contentLength, InputStream content, long duration) throws IOException {
    Delayer delayer = new Delayer(duration);
    long delay = Math.max(duration / (contentLength + 1), 1);
    delayer.delay(delay);
    for (int b = content.read(); b > -1; b = content.read()) {
      os.write(b);
      os.flush();
      delayer.delay(delay);
    }
    delayer.delay(duration);
  }
}
