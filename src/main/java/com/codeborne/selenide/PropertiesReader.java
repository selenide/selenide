package com.codeborne.selenide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

final class PropertiesReader {
  private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);

  private final String fileName;
  private Properties properties;

  PropertiesReader(String fileName) {
    this.fileName = fileName;
  }

  public synchronized String getProperty(String key, String defaultValue) {
    if (properties == null) {
      properties = loadSelenideProperties();
    }
    return properties.getProperty(key, defaultValue);
  }

  private Properties loadSelenideProperties() {
    Properties properties = new Properties();
    loadPropertiesFrom(Thread.currentThread().getContextClassLoader(), properties);
    properties.putAll(System.getProperties());
    return properties;
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
