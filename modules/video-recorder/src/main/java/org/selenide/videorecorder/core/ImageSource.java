package org.selenide.videorecorder.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FileUtils.openInputStream;

public interface ImageSource {
  InputStream getImage() throws IOException;
}

record FileImageSource(File file) implements ImageSource {
  @Override
  public InputStream getImage() throws IOException {
    return openInputStream(file);
  }

  @Override
  public String toString() {
    return file.toString();
  }
}

record ClasspathResource(String file) implements ImageSource {
  @Override
  public InputStream getImage() {
    return requireNonNull(currentThread().getContextClassLoader().getResourceAsStream(file),
      () -> "Resource " + file + " not found in classpath");
  }

  @Override
  public String toString() {
    return "cp:" + file;
  }
}
