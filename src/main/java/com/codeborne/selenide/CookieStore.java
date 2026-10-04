package com.codeborne.selenide;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Set;

public class CookieStore implements Conditional<CookieStore> {
  private final Driver driver;

  CookieStore(Driver driver) {
    this.driver = driver;
  }

  @Override
  public Driver driver() {
    return driver;
  }

  @Override
  public CookieStore object() {
    return this;
  }

  /**
   * Delete all the cookies for the current domain.
   * @see WebDriver.Options#deleteAllCookies()
   */
  public void clear() {
    driver.getWebDriver().manage().deleteAllCookies();
  }

  /**
   * Delete the named cookie from the current domain.
   * @see WebDriver.Options#deleteCookieNamed(String)
   */
  public void delete(String name) {
    driver.getWebDriver().manage().deleteCookieNamed(name);
  }

  /**
   * Get all the cookies for the current domain.
   * @see WebDriver.Options#getCookies()
   */
  public Set<Cookie> getAll() {
    return driver.getWebDriver().manage().getCookies();
  }

  /**
   * Get cookie with given name for the current domain.
   * @see WebDriver.Options#getCookies()
   */
  @Nullable
  public Cookie get(String name) {
    return driver.getWebDriver().manage().getCookieNamed(name);
  }

  public void add(String name, String value) {
    add(new Cookie(name, value, null));
  }

  public void add(Cookie cookie) {
    driver.getWebDriver().manage().addCookie(cookie);
  }

  public int size() {
    return getAll().size();
  }
}
