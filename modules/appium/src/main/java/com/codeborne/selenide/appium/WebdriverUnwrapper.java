package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;
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

  public static <T> boolean instanceOf(Driver driver, Class<T> klass) {
    return cast(driver, klass).isPresent();
  }

  public static <T> boolean instanceOf(WebDriver driver, Class<T> klass) {
    return cast(driver, klass).isPresent();
  }

  public static <T> Optional<T> cast(Driver driver, Class<T> klass) {
    return cast(driver.getWebDriver(), klass);
  }

  public static <T> Optional<T> cast(WebDriver probablyWrappedWebdriver, Class<T> klass) {
    WebDriver unwrappedWebdriver = probablyWrappedWebdriver instanceof WrapsDriver ?
      ((WrapsDriver) probablyWrappedWebdriver).getWrappedDriver() :
      probablyWrappedWebdriver;

    //noinspection unchecked
    return klass.isAssignableFrom(unwrappedWebdriver.getClass()) ?
      Optional.of((T) unwrappedWebdriver) :
      Optional.empty();
  }

  public static <T> T cast(WebElement probablyWrappedWebElement, Class<T> klass) {
    WebElement unwrappedWebElement = probablyWrappedWebElement instanceof WrapsElement ?
      ((WrapsElement) probablyWrappedWebElement).getWrappedElement() :
      probablyWrappedWebElement;

    if (!klass.isAssignableFrom(unwrappedWebElement.getClass())) {
      throw new IllegalArgumentException("WebElement " + unwrappedWebElement + " is not instance of " + klass.getName());
    }
    //noinspection unchecked
    return (T) unwrappedWebElement;
  }
}
