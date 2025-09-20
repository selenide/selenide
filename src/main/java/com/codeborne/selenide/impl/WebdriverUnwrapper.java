package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Optional;

public class WebdriverUnwrapper {
  /**
   * @deprecated instead of using this method, just
   * cast your {@link WebDriver} to the needed interface, e.g. {@link JavascriptExecutor} or {@link TakesScreenshot}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("DeprecatedIsStillUsed")
  public static <T> boolean instanceOf(Driver driver, Class<T> klass) {
    return cast(driver, klass).isPresent();
  }

  /**
   * @deprecated instead of using this method, just
   * cast your {@link WebDriver} to the needed interface, e.g. {@link JavascriptExecutor} or {@link TakesScreenshot}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("DeprecatedIsStillUsed")
  public static <T> boolean instanceOf(SearchContext driver, Class<T> klass) {
    return cast(driver, klass).isPresent();
  }

  /**
   * @deprecated instead of using this method, just
   * cast your {@link WebDriver} to the needed interface, e.g. {@link JavascriptExecutor} or {@link TakesScreenshot}.
   */
  @Deprecated(forRemoval = true)
  public static <T> Optional<T> cast(Driver driver, Class<T> klass) {
    return cast(driver.getWebDriver(), klass);
  }

  /**
   * @deprecated instead of using this method, just
   * cast your {@link WebDriver} to the needed interface, e.g. {@link JavascriptExecutor} or {@link TakesScreenshot}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("unchecked")
  public static <T> Optional<T> cast(SearchContext driverOrElement, Class<T> klass) {
    if (klass.isAssignableFrom(driverOrElement.getClass())) {
      return Optional.of((T) driverOrElement);
    }
    WebDriver webdriver = unwrap(driverOrElement);
    return webdriver != null && klass.isAssignableFrom(webdriver.getClass()) ? Optional.of((T) webdriver) : Optional.empty();
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

  /**
   * @deprecated instead of using this method, just
   * cast your {@link WebDriver} to the needed interface, e.g. {@link JavascriptExecutor} or {@link TakesScreenshot}.
   */
  @Deprecated(forRemoval = true)
  @Nullable
  public static WebDriver unwrap(SearchContext driverOrElement) {
    if (driverOrElement instanceof WrapsDriver wrapper) {
      return unwrap(wrapper.getWrappedDriver());
    }
    return (WebDriver) driverOrElement;
  }

  public static RemoteWebDriver unwrapRemoteWebDriver(WebDriver driver) {
    return driver instanceof WrapsDriver wrapper ? (RemoteWebDriver) wrapper.getWrappedDriver() : (RemoteWebDriver) driver;
  }
}
