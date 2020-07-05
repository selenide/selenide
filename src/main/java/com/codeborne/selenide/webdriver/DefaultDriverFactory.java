package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.WebDriverProvider;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@ParametersAreNonnullByDefault
public class DefaultDriverFactory extends AbstractDriverFactory {
  @Override
  public void setupWebdriverBinary() {
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
    return createInstanceOf(config.browser(), config, browser, proxy, browserDownloadsFolder);
  }

  @CheckReturnValue
  @Nonnull
  private WebDriver createInstanceOf(String className, Config config, Browser browser,
                                     @Nullable Proxy proxy, File browserDownloadsFolder) {
    try {
      Capabilities capabilities = createCapabilities(config, browser, proxy, browserDownloadsFolder);

      Class<?> clazz = Class.forName(className);
      if (WebDriverProvider.class.isAssignableFrom(clazz)) {
        return createInstanceOf(WebDriverProvider.class, clazz).createDriver(new DesiredCapabilities(capabilities));
      }
      else if (DriverFactory.class.isAssignableFrom(clazz)) {
        DriverFactory factory = createInstanceOf(DriverFactory.class, clazz);
        if (config.driverManagerEnabled()) {
          factory.setupWebdriverBinary();
        }
        return factory.create(config, browser, proxy, browserDownloadsFolder);
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

  @Override
  @CheckReturnValue
  @Nonnull
  public MutableCapabilities createCapabilities(Config config, Browser browser, @Nullable Proxy proxy, File browserDownloadsFolder) {
    return createCommonCapabilities(config, browser, proxy);
  }

  @SuppressWarnings({"unchecked", "unused"})
  private <T> T createInstanceOf(Class<T> resultClass, Class<?> clazz) throws Exception {
    Constructor<?> constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);
    return (T) constructor.newInstance();
  }

  private RuntimeException runtime(Throwable exception) {
    return exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
  }
}
