package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static java.lang.Thread.currentThread;

public class ElementFinder extends WebElementSource {
  public static SelenideElement wrap(Context context, WebElement parent, String cssSelector) {
    return wrap(context, parent, By.cssSelector(cssSelector), 0);
  }

  public static SelenideElement wrap(Context context, String cssSelector, int index) {
    return wrap(context, null, By.cssSelector(cssSelector), index);
  }

  public static SelenideElement wrap(Context context, WebElement parent, String cssSelector, int index) {
    return wrap(context, WebElementWrapper.wrap(context, parent), By.cssSelector(cssSelector), index);
  }

  public static SelenideElement wrap(Context context, By criteria) {
    return wrap(context, null, criteria, 0);
  }

  public static SelenideElement wrap(Context context, SearchContext parent, By criteria, int index) {
    return wrap(context, SelenideElement.class, parent, criteria, index);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Context context, Class<T> clazz, SearchContext parent, By criteria, int index) {
    return (T) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{clazz},
        new SelenideElementProxy(new ElementFinder(context, parent, criteria, index)));
  }

  private final Context context;
  private final SearchContext parent;
  private final By criteria;
  private final int index;

  ElementFinder(Context context, SearchContext parent, By criteria, int index) {
    this.context = context;
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
  }

  @Override
  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return arg instanceof By ?
        wrap(context, proxy, (By) arg, index) :
        wrap(context, proxy, By.cssSelector((String) arg), index);
  }

  @Override
  public Context context() {
    return context;
  }

  @Override
  public WebElement getWebElement() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        WebElementSelector.instance.findElement(context, getSearchContext(), criteria) :
        WebElementSelector.instance.findElements(context, getSearchContext(), criteria).get(index);
  }

  @Override
  public List<WebElement> findAll() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        WebElementSelector.instance.findElements(context(), getSearchContext(), criteria) :
        super.findAll();
  }

  private SearchContext getSearchContext() {
    return parent == null ? context().getWebDriver() :
        (parent instanceof SelenideElement) ? ((SelenideElement) parent).toWebElement() :
        parent;
  }

  @Override
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    if (parent instanceof SelenideElement) {
      ((SelenideElement) parent).should(exist);
    }
    else if (parent instanceof WebElement) {
      WebElementWrapper.wrap(context(), (WebElement) parent).should(exist);
    }
    
    return super.createElementNotFoundError(condition, lastError);
  }

  @Override
  public String getSearchCriteria() {
    return index == 0 ? 
        Describe.selector(criteria) : 
        Describe.selector(criteria) + '[' + index + ']';
  }

  @Override
  public String toString() {
    return "{" + getSearchCriteria() + '}';
  }
}
