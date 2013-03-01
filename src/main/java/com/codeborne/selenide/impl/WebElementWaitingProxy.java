package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.defaultWaitingTimeout;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;

public class WebElementWaitingProxy extends AbstractSelenideElementProxy {
  public static SelenideElement wrap(WebElement parent, By criteria, int index) {
    return (SelenideElement) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{SelenideElement.class},
        new WebElementWaitingProxy(parent, criteria, index));
  }

  private final WebElement parent;
  private final By criteria;
  private final int index;

  WebElementWaitingProxy(WebElement parent, By criteria, int index) {
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
  }

  @Override
  protected WebElement getDelegate() {
    return waitUntil(exist, defaultWaitingTimeout);
  }

  @Override
  protected SelenideElement find(Object arg, int index) {
    return arg instanceof By ?
        wrap(getDelegate(), (By) arg, index) :
        wrap(getDelegate(), By.cssSelector((String) arg), index);
  }

  @Override
  protected WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        getSearchContext().findElement(criteria) :
        getSearchContext().findElements(criteria).get(index);
  }

  private SearchContext getSearchContext() {
    return parent == null ? getWebDriver() : parent;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '{' + criteria +
        (parent == null ? "" : ", in: " + parent) +
        (index == 0 ? "" : ", index: " + index) +
        '}';
  }
}
