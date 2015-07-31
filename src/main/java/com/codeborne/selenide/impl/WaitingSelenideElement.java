package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
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
    return getActualDelegate();
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
        WebElementSelector.instance.findElement(getSearchContext(), criteria) :
        WebElementSelector.instance.findElements(getSearchContext(), criteria).get(index);
  }

  private SearchContext getSearchContext() {
    return parent == null ? getWebDriver() :
        (parent instanceof SelenideElement) ? ((SelenideElement) parent).toWebElement() :
        parent;
  }

  @Override
  protected ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    if (parent instanceof SelenideElement) {
      ((SelenideElement) parent).should(exist);
    }
    else if (parent instanceof WebElement) {
      $((WebElement) parent).should(exist);
    }
    
    return super.createElementNotFoundError(condition, lastError);
  }

  @Override
  String getSearchCriteria() {
    return index == 0 ? 
        Describe.selector(criteria) : 
        Describe.selector(criteria) + '[' + index + ']';
  }

  @Override
  public String toString() {
    return "{" + getSearchCriteria() + '}';
  }
}
