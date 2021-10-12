package com.codeborne.selenide.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

  private static final String SELENIDE_PROPERTIES_FILE = "selenide.properties";

  private PropertiesUtils() {
  }

  public static Properties loadSelenideProperties() {
    final Properties properties = new Properties();
    loadPropertiesFrom(ClassLoader.getSystemClassLoader(), properties);
    loadPropertiesFrom(Thread.currentThread().getContextClassLoader(), properties);
    properties.putAll(System.getProperties());
    return properties;
  }

  private static void loadPropertiesFrom(final ClassLoader classLoader, final Properties properties) {
    try (InputStream stream = classLoader.getResourceAsStream(SELENIDE_PROPERTIES_FILE)) {
      if (stream != null) {
        properties.load(stream);
      }
    } catch (IOException e) {
      LOGGER.error("Error while reading selenide.properties file from classpath: {}", e.getMessage());
    }
  }
}
