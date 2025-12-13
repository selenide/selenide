package integration.server;

import java.io.InputStream;
import java.util.Random;

class ContentGenerator extends InputStream {
  private final long contentLength;
  private long bytesCount;
  private final Random random = new Random();

  ContentGenerator(long contentLength) {
    this.contentLength = contentLength;
  }

  @Override
  public int read() {
    if (bytesCount < contentLength) {
      bytesCount++;
      return random.nextInt(96, 96 + 26);
    }
    return -1;
  }
}
