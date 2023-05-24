package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.WrapsElement;

import java.util.Optional;

/**
 * A temporary solution to fix <a href="https://github.com/selenide/selenide-appium/issues/72">...</a>
 * Will be replaced by a better solution in Selenide.
 */
public class WebdriverUnwrapper {
  public static boolean isMobile(Driver driver) {
    return instanceOf(driver, AppiumDriver.class);
  }

  public static boolean isMobile(SearchContext driver) {
    return instanceOf(driver, AppiumDriver.class);
  }

  public static <T> boolean instanceOf(Driver driver, Class<T> klass) {
    return cast(driver, klass).isPresent();
  }

  public static <T> boolean instanceOf(SearchContext driver, Class<T> klass) {
    return cast(driver, klass).isPresent();
  }

  public static <T> Optional<T> cast(Driver driver, Class<T> klass) {
    return cast(driver.getWebDriver(), klass);
  }

  public static <T> Optional<T> cast(SearchContext driverOrElement, Class<T> klass) {
    SearchContext unwrappedWebdriver = driverOrElement;
    while (unwrappedWebdriver instanceof WrapsDriver wrapper) {
      unwrappedWebdriver = wrapper.getWrappedDriver();
    }

    //noinspection unchecked
    return klass.isAssignableFrom(unwrappedWebdriver.getClass()) ?
      Optional.of((T) unwrappedWebdriver) :
      Optional.empty();
  }

  public static <T> T cast(WebElement probablyWrappedWebElement, Class<T> klass) {
    WebElement unwrappedWebElement = probablyWrappedWebElement;
    while (unwrappedWebElement instanceof WrapsElement wrapper) {
      unwrappedWebElement = wrapper.getWrappedElement();
    }

    if (!klass.isAssignableFrom(unwrappedWebElement.getClass())) {
      throw new IllegalArgumentException("WebElement " + unwrappedWebElement + " is not instance of " + klass.getName());
    }
    //noinspection unchecked
    return (T) unwrappedWebElement;
  }
}
