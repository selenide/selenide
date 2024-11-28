package com.codeborne.selenide;

import java.io.File;
import java.net.URL;

import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;

public class TestResources {
  public static URL toURL(String resource) {
    return find(resource);
  }

  public static File toFile(String resource) {
    return new File(find(resource).getPath());
  }

  private static URL find(String resource) {
    return requireNonNull(
      currentThread().getContextClassLoader().getResource(resource),
      () -> "Not found in classpath: " + resource
    );
  }
}
