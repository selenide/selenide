package integration.server;

import java.io.Serializable;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UploadedFile implements Serializable {
  private final String name;
  private final byte[] content;

  UploadedFile(String name, byte[] content) {
    this.name = name;
    this.content = content;
  }

  public String name() {
    return name;
  }

  public String content() {
    return new String(content, UTF_8);
  }
}
