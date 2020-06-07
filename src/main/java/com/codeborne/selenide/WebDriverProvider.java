package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * <p>
 *   Interface for using custom WebDriver in your tests
 * </p>
 *
 * <p>
 * To customize {@link WebDriver} creation one can use any of the alternatives:
 * <ul>
 * <li>Call method {@link com.codeborne.selenide.WebDriverRunner#setWebDriver(WebDriver)} explicitly.
 * <li>Extend {@link WebDriver} implementation, override {@code public XxxDriver(Capabilities desiredCapabilities)}
 * constructor and pass this class name as {@code browser} system variable value.
 * <li>Implement this very interface and pass the implementation class name as {@code browser} system variable value.
 * </ul>
 */
public interface WebDriverProvider {

  /**
   * Create new {@link WebDriver} instance. The instance will be bound to current thread, so there is no need to cache
   * this instance in method implementation. Also don't cache the instance in static variable, as <a
   * href="http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Is_WebDriver_thread-safe?">WebDriver
   * instance is not thread-safe</a>.
   *
   * @param desiredCapabilities set of desired capabilities as suggested by Selenide framework; method implementation is
   * recommended to pass this variable to {@link WebDriver}, probably modifying it according to specific needs
   * @return new {@link WebDriver} instance
   */
  @CheckReturnValue
  @Nonnull
  WebDriver createDriver(@Nonnull DesiredCapabilities desiredCapabilities);
}
