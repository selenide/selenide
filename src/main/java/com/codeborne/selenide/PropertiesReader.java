package com.codeborne.selenide;

import com.codeborne.selenide.impl.Lazy;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;

final class PropertiesReader {
  private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);

  private final String fileName;
  private final Lazy<Properties> properties = lazyEvaluated(() -> loadSelenideProperties());

  PropertiesReader(String fileName) {
    this.fileName = fileName;
  }

  @Nullable
  public synchronized String getPropertyOrNull(String key) {
    return properties.get().getProperty(key, null);
  }

  public synchronized String getProperty(String key, String defaultValue) {
    return properties.get().getProperty(key, defaultValue);
  }

  private Properties loadSelenideProperties() {
    Properties result = new Properties();
    loadPropertiesFrom(Thread.currentThread().getContextClassLoader(), result);
    result.putAll(System.getProperties());
    return result;
  }

  private void loadPropertiesFrom(ClassLoader classLoader, Properties properties) {
    try (InputStream stream = classLoader.getResourceAsStream(fileName)) {
      if (stream != null) {
        log.debug("Reading settings from {}", classLoader.getResource(fileName));
        properties.load(stream);
      }
    }
    catch (IOException e) {
      throw new UncheckedIOException("Failed to read " + fileName + " file from classpath", e);
    }
  }
}
