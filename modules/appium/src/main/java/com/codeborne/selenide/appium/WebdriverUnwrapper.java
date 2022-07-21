package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

import java.util.Optional;

/**
 * A temporary solution to fix <a href="https://github.com/selenide/selenide-appium/issues/72">...</a>
 * Will be replaced by a better solution in Selenide.
 */
public class WebdriverUnwrapper {
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
}
