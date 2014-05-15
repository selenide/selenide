package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;

public class WaitingSelenideElement extends AbstractSelenideElement {
  public static SelenideElement wrap(By criteria) {
    return wrap(null, criteria, 0);
  }

  public static SelenideElement wrap(SearchContext parent, By criteria, int index) {
    return (SelenideElement) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{SelenideElement.class},
        new WaitingSelenideElement(parent, criteria, index));
  }

  private final SearchContext parent;
  private final By criteria;
  private final int index;

  WaitingSelenideElement(SearchContext parent, By criteria, int index) {
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
  }

  @Override
  protected WebElement getDelegate() {
    return waitUntil("", exist, timeout);
  }

  @Override
  protected SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return arg instanceof By ?
        wrap(proxy, (By) arg, index) :
        wrap(proxy, By.cssSelector((String) arg), index);
  }

  @Override
  protected WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        getSearchContext().findElement(criteria) :
        getSearchContext().findElements(criteria).get(index);
  }

  private SearchContext getSearchContext() {
    return parent == null ? getWebDriver() :
        (parent instanceof SelenideElement) ? ((SelenideElement)parent).toWebElement() :
        parent;
  }

  @Override
  protected WebElement throwElementNotFound(Condition condition, long timeoutMs) {
    if (parent instanceof SelenideElement) {
      ((SelenideElement) parent).shouldBe(visible);
    }
    else if (parent instanceof WebElement) {
      $((WebElement) parent).shouldBe(visible);
    }
    
    return super.throwElementNotFound(condition, timeoutMs);
  }

  @Override
  String getSearchCriteria() {
    return index == 0 ? criteria.toString() : criteria.toString() + '[' + index + ']';
  }

  @Override
  public String toString() {
    return "{" + getSearchCriteria() + '}';
  }
}
