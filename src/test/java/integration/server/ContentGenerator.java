package integration.server;

import java.io.InputStream;

class ContentGenerator extends InputStream {
  private final int contentLength;
  private int bytesCount;

  ContentGenerator(int contentLength) {
    this.contentLength = contentLength;
  }

  @Override
  public int read() {
    if (bytesCount < contentLength) {
      bytesCount++;
      return bytesCount % 26 + 96;
    }
    return -1;
  }
}
