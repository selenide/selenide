package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import static java.lang.Thread.currentThread;

public class ElementSupplier extends WebElementSource {

  private final Supplier<? extends WebElement> howToGetElement;

  ElementSupplier(Supplier<? extends WebElement> howToGetElement) {
    this.howToGetElement = howToGetElement;
  }

  public static SelenideElement wrap(Supplier<? extends WebElement> howToGetElement) {
    return wrap(SelenideElement.class, howToGetElement);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz, Supplier<? extends WebElement> howToGetElement) {
    return (T) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{clazz},
        new SelenideElementProxy(new ElementSupplier(howToGetElement)));
  }

  @Override
  public WebElement getWebElement() {
    return this.howToGetElement.get();
  }

  @Override
  public String getSearchCriteria() {
    try {
      return Describe.describe(howToGetElement.get());
    } catch (Exception e) {
      return e.getMessage();
    }

  }

  @Override
  public String toString() {
    return "{" + getSearchCriteria() + '}';
  }
}
