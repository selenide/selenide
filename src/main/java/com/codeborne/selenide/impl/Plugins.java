package com.codeborne.selenide.impl;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * We assume this API will change in next releases.
 * Be aware if you are going to use it.
 *
 * @since Selenide 5.15.0
 */
public class Plugins {
  private static final Logger logger = LoggerFactory.getLogger(Plugins.class);
  private static final Map<Class<?>, Object> cache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static synchronized <T> T inject(Class<T> klass) {
    T plugin = (T) cache.get(klass);
    if (plugin == null) {
      plugin = loadPlugin(klass);
      cache.put(klass, plugin);
    }
    return plugin;
  }

  private static <T> T loadPlugin(Class<T> klass) {
    Iterator<T> loader = ServiceLoader.load(klass).iterator();
    if (!loader.hasNext()) {
      T defaultPlugin = getDefaultPlugin(klass);
      logger.debug("Using default implementation of {}: {}", klass.getName(), defaultPlugin.getClass().getName());
      return defaultPlugin;
    }
    T implementation = loader.next();
    logger.info("Using implementation of {}: {}", klass.getName(), implementation.getClass().getName());
    return implementation;
  }

  private static <T> T getDefaultPlugin(Class<T> klass) {
    String resource = "/META-INF/defaultservices/" + klass.getName();
    URL file = Plugins.class.getResource(resource);
    if (file == null) {
      throw new IllegalStateException("Resource not found in classpath: " + resource);
    }

    String className = readFile(file).trim();
    try {
      return instantiate(className);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to initialize default plugin " + className + " from " + file, e);
    }
  }

  @Nonnull
  private static <T> T instantiate(String className) throws Exception {
    @SuppressWarnings("unchecked")
    Constructor<T> constructor = (Constructor<T>) Class.forName(className).getDeclaredConstructor();
    constructor.setAccessible(true);
    return constructor.newInstance();
  }

  private static String readFile(URL file) {
    try {
      return IOUtils.toString(file, UTF_8);
    }
    catch (IOException e) {
      throw new IllegalStateException("Failed to read " + file, e);
    }
  }
}
