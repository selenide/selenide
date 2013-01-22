package com.codeborne.selenide.impl;

import com.codeborne.selenide.DOM;
import com.codeborne.selenide.ShouldableWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;

public class WebElementWaitingProxy extends AbstractShouldableWebElementProxy {
  public static ShouldableWebElement wrap(WebElement parent, By criteria, int index) {
    return (ShouldableWebElement) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{ShouldableWebElement.class}, new WebElementWaitingProxy(parent, criteria, index));
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
    return waitUntil(exist, DOM.defaultWaitingTimeout);
  }

  @Override
  protected ShouldableWebElement find(Object arg, int index) {
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
