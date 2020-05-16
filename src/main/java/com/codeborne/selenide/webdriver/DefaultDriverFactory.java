package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultDriverFactory extends AbstractDriverFactory {
  @Override
  boolean supports(Config config, Browser browser) {
    return true;
  }

  @Override
  public void setupBinary() {
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    return createInstanceOf(config.browser(), config, browser, proxy);
  }

  private WebDriver createInstanceOf(String className, Config config, Browser browser, Proxy proxy) {
    try {
      DesiredCapabilities capabilities = createCommonCapabilities(config, browser, proxy);

      Class<?> clazz = Class.forName(className);
      if (WebDriverProvider.class.isAssignableFrom(clazz)) {
        return createInstanceOf(WebDriverProvider.class, clazz).createDriver(capabilities);
      }
      else if (DriverFactory.class.isAssignableFrom(clazz)) {
        DriverFactory factory = createInstanceOf(DriverFactory.class, clazz);
        if (config.driverManagerEnabled()) {
          factory.setupBinary();
        }
        return factory.create(config, browser, proxy);
      }
      else {
        Constructor<?> constructor = Class.forName(className).getConstructor(Capabilities.class);
        return (WebDriver) constructor.newInstance(capabilities);
      }
    } catch (InvocationTargetException e) {
      throw runtime(e.getTargetException());
    } catch (Exception invalidClassName) {
      throw new IllegalArgumentException(invalidClassName);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T createInstanceOf(Class<T> resultClass, Class<?> clazz) throws Exception {
    Constructor<?> constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);
    return (T) constructor.newInstance();
  }

  private RuntimeException runtime(Throwable exception) {
    return exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
  }
}
