package com.codeborne.selenide.impl;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * Read file content from classpath
 *
 * The point is in lazy loading: the content is loaded only on the first usage, and only once.
 */
public class FileContent {
  private final String filePath;
  private final Lazy<String> content = lazyEvaluated(() -> readContent());

  public FileContent(String filePath) {
    this.filePath = filePath;
  }

  public String content() {
    return content.get();
  }

  private String readContent() {
    try {
      URL url = requireNonNull(currentThread().getContextClassLoader().getResource(filePath));
      return IOUtils.toString(url, UTF_8);
    }
    catch (IOException e) {
      throw new IllegalArgumentException("Cannot load " + filePath + " from classpath", e);
    }
  }
}
