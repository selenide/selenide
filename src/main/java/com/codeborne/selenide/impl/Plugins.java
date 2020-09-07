package com.codeborne.selenide.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * We assume this API will change in next releases.
 * Be aware if you are going to use it.
 *
 * @since Selenide 5.15.0
 */
public class Plugins {
  private static final Logger logger = LoggerFactory.getLogger(Plugins.class);
  private static final Map<Class<?>, Object> cache = new ConcurrentHashMap<>();

  public static WebElementPrinter getWebElementPrinter() {
    return getPlugin(WebElementPrinter.class, com.codeborne.selenide.impl.SelenideWebElementPrinter.class);
  }

  @SuppressWarnings("unchecked")
  private static <T> T getPlugin(Class<T> klass, Class<? extends T> defaultImplementation) {
    return (T) cache.computeIfAbsent(klass, (c) -> loadPlugin(klass, defaultImplementation));
  }

  private static <T> T loadPlugin(Class<T> klass, Class<? extends T> defaultImplementation) {
    Iterator<T> loader = ServiceLoader.load(klass).iterator();
    if (!loader.hasNext()) {
      logger.debug("Using default implementation of {}: {}", klass.getName(), defaultImplementation.getName());
      try {
        return defaultImplementation.getConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        throw new IllegalStateException("Failed to initialize " + defaultImplementation.getName(), e);
      }
    }
    T implementation = loader.next();
    logger.info("Using implementation of {}: {}", klass.getName(), implementation.getClass().getName());
    return implementation;
  }
}
