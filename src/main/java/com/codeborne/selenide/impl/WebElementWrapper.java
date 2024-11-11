package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("java:S5852")
public class WebElementWrapper extends WebElementSource {

  private static final Pattern RE_DECORATED = Pattern.compile("Decorated \\{(.+)}");
  private static final Pattern RE_WRAPPED_SELENIDE_ELEMENT = Pattern.compile("Proxy element for: DefaultElementLocator '(.+)'");
  private static final Pattern RE_REMOTE_WEB_ELEMENT = Pattern.compile("\\[\\[.+] -> (.+)]");

  public static SelenideElement wrap(Driver driver, WebElement element) {
    return wrap(SelenideElement.class, driver, element);
  }

  public static <T extends SelenideElement> T wrap(Class<T> clazz, Driver driver, WebElement element) {
    return wrap(clazz, driver, element, null);
  }

  public static SelenideElement wrap(Driver driver, WebElement element, @Nullable String searchCriteria) {
    return wrap(SelenideElement.class, driver, element, searchCriteria);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz, Driver driver,
                                                   WebElement element, @Nullable String searchCriteria) {
    return clazz.isInstance(element) ?
      clazz.cast(element) :
      (T) Proxy.newProxyInstance(
        element.getClass().getClassLoader(), new Class<?>[]{clazz},
        new SelenideElementProxy<>(new WebElementWrapper(driver, element, searchCriteria)));
  }

  private final Driver driver;
  private final WebElement delegate;
  @Nullable private final String searchCriteria;

  protected WebElementWrapper(Driver driver, WebElement delegate, @Nullable String searchCriteria) {
    this.driver = driver;
    this.delegate = delegate;
    this.searchCriteria = searchCriteria;
  }

  @Override
  public WebElement getWebElement() {
    return delegate;
  }

  @Override
  public String getSearchCriteria() {
    if (searchCriteria != null) return searchCriteria;
    String elementToString = delegate.toString();
    Matcher m1 = RE_DECORATED.matcher(elementToString);
    String undecorated = m1.matches() ? m1.replaceFirst("$1") : elementToString;

    Matcher m2 = RE_REMOTE_WEB_ELEMENT.matcher(undecorated);
    if (m2.matches()) return m2.replaceFirst("{$1}");

    Matcher m3 = RE_WRAPPED_SELENIDE_ELEMENT.matcher(undecorated);
    if (m3.matches()) return m3.replaceFirst("{$1}");

    return undecorated;
  }

  @Override
  public String toString() {
    return getAlias().getOrElse(() -> getSearchCriteria());
  }

  @Override
  public Driver driver() {
    return driver;
  }
}
