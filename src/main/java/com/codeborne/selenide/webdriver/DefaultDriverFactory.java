package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.WebDriverProvider;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultDriverFactory extends AbstractDriverFactory {

  @Override
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    return createInstanceOf(config.browser(), config, browser, proxy, browserDownloadsFolder);
  }

  private WebDriver createInstanceOf(String className, Config config, Browser browser,
                                     @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    Class<?> clazz = classOf(config.browser());

    if (WebDriverProvider.class.isAssignableFrom(clazz)) {
      Capabilities capabilities = createCapabilities(config, browser, proxy, browserDownloadsFolder);
      return createInstanceOf(WebDriverProvider.class, clazz).createDriver(capabilities);
    }
    else if (DriverFactory.class.isAssignableFrom(clazz)) {
      DriverFactory factory = createInstanceOf(DriverFactory.class, clazz);
      return factory.create(config, browser, proxy, browserDownloadsFolder);
    }
    else {
      Capabilities capabilities = createCapabilities(config, browser, proxy, browserDownloadsFolder);
      return createWebDriver(className, capabilities);
    }
  }

  @Override
  public MutableCapabilities createCapabilities(Config config, Browser browser,
                                                @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    Class<?> clazz = classOf(config.browser());

    if (DriverFactory.class.isAssignableFrom(clazz)) {
      DriverFactory factory = createInstanceOf(DriverFactory.class, clazz);
      return factory.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    }

    return createCommonCapabilities(config, browser, proxy);
  }

  private Class<?> classOf(String className) {
    try {
      return Class.forName(className);
    }
    catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Class not found: " + className, e);
    }
  }

  private WebDriver createWebDriver(String className, Capabilities capabilities) {
    try {
      Constructor<?> constructor = Class.forName(className).getConstructor(Capabilities.class);
      return (WebDriver) constructor.newInstance(capabilities);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to create WebDriver of type " + className, e);
    }
  }

  @SuppressWarnings({"unchecked", "unused", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
  private <T> T createInstanceOf(Class<T> resultClass, Class<?> clazz) {
    try {
      Constructor<?> constructor = clazz.getDeclaredConstructor();
      constructor.setAccessible(true);
      return (T) constructor.newInstance();
    }
    catch (InvocationTargetException e) {
      throw runtime(e.getTargetException());
    }
    catch (Exception invalidClassName) {
      throw new IllegalArgumentException(invalidClassName);
    }
  }

  private RuntimeException runtime(Throwable exception) {
    return exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
  }
}
