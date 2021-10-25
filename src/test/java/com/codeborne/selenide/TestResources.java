package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.net.URL;

import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class TestResources {
  public static URL toURL(String resource) {
    return find(resource);
  }

  public static File toFile(String resource) {
    return new File(find(resource).getPath());
  }

  @Nonnull
  @CheckReturnValue
  private static URL find(String resource) {
    return requireNonNull(
      currentThread().getContextClassLoader().getResource(resource),
      () -> "Not found in classpath: " + resource
    );
  }
}
