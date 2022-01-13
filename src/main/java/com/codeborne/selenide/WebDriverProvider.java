package com.codeborne.selenide;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * <p>
 *  Interface for using custom WebDriver in your tests.
 * </p>
 *
 * <p>
 *   See https://github.com/selenide/selenide/wiki/How-Selenide-creates-WebDriver for overview
 * </p>
 *
 * <p>
 * To customize {@link WebDriver} creation one can use any of the alternatives:
 * <ul>
 * <li>Call method {@link com.codeborne.selenide.WebDriverRunner#setWebDriver(WebDriver)} explicitly.
 * <li>Extend {@link com.codeborne.selenide.webdriver.DriverFactory} implementation
 * <li>Extend {@link WebDriver} implementation, override {@code public XxxDriver(Capabilities capabilities)}
 * constructor and pass this class name as {@code browser} system variable value.
 * <li>Implement this very interface and pass the implementation class name as {@code browser} system variable value.
 * </ul>
 *
 * <p>
 *  Nowadays {@link com.codeborne.selenide.webdriver.DriverFactory} is probably preferred to {@link WebDriverProvider} because
 *  the latter cannot use Selenide proxy and downloads folder. If you don't need them - it's fine to use {@link WebDriverProvider} too.
 * </p>
 */
@ParametersAreNonnullByDefault
public interface WebDriverProvider {

  /**
   * Create new {@link WebDriver} instance. The instance will be bound to current thread, so there is no need to cache
   * this instance in method implementation. Also don't cache the instance in static variable, as <a
   * href="http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Is_WebDriver_thread-safe?">WebDriver
   * instance is not thread-safe</a>.
   *
   * @param capabilities set of desired capabilities as suggested by Selenide framework; method implementation is
   * recommended to pass this variable to {@link WebDriver}, probably modifying it according to specific needs
   * @return new {@link WebDriver} instance
   */
  @CheckReturnValue
  @Nonnull
  WebDriver createDriver(@Nonnull Capabilities capabilities);
}
