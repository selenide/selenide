package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.util.Optional;

public class WebdriverUnwrapper {
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
    WebDriver webdriver = unwrap(driverOrElement);

    //noinspection unchecked
    return webdriver != null && klass.isAssignableFrom(webdriver.getClass()) ?
      Optional.of((T) webdriver) :
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

  @Nullable
  @CheckReturnValue
  public static WebDriver unwrap(SearchContext driverOrElement) {
    if (driverOrElement instanceof WrapsDriver wrapper) {
      return unwrap(wrapper.getWrappedDriver());
    }
    if (driverOrElement instanceof RemoteWebDriver remoteWebDriver) {
      return new Augmenter().augment(remoteWebDriver);
    }
    return null;
  }
}
