package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.lang.Thread.currentThread;

@ParametersAreNonnullByDefault
public class ElementFinder extends WebElementSource {
  private final ElementDescriber describe = inject(ElementDescriber.class);

  @CheckReturnValue
  @Nonnull
  public static SelenideElement wrap(Driver driver, WebElement parent, String cssSelector) {
    return wrap(driver, parent, By.cssSelector(cssSelector), 0);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideElement wrap(Driver driver, String cssSelector, int index) {
    return wrap(driver, null, By.cssSelector(cssSelector), index);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideElement wrap(Driver driver, WebElement parent, String cssSelector, int index) {
    return wrap(driver, WebElementWrapper.wrap(driver, parent), By.cssSelector(cssSelector), index);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideElement wrap(Driver driver, By criteria) {
    return wrap(driver, null, criteria, 0);
  }

  @CheckReturnValue
  @Nonnull
  public static SelenideElement wrap(Driver driver, @Nullable SearchContext parent, By criteria, int index) {
    return wrap(driver, SelenideElement.class, parent, criteria, index);
  }

  @CheckReturnValue
  @Nonnull
  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Driver driver,
                                                   Class<T> clazz,
                                                   @Nullable SearchContext parent,
                                                   By criteria,
                                                   int index) {
    return (T) Proxy.newProxyInstance(
      currentThread().getContextClassLoader(),
      new Class<?>[]{clazz},
      new SelenideElementProxy(new ElementFinder(driver, parent, criteria, index)));
  }

  private final Driver driver;
  private final SearchContext parent;
  private final By criteria;
  private final int index;

  ElementFinder(Driver driver, @Nullable SearchContext parent, By criteria, int index) {
    this.driver = driver;
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return arg instanceof By ?
        wrap(driver, proxy, (By) arg, index) :
        wrap(driver, proxy, By.cssSelector((String) arg), index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return driver;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getWebElement() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        WebElementSelector.instance.findElement(driver, getSearchContext(), criteria) :
        WebElementSelector.instance.findElements(driver, getSearchContext(), criteria).get(index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> findAll() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        WebElementSelector.instance.findElements(driver(), getSearchContext(), criteria) :
        super.findAll();
  }

  @CheckReturnValue
  @Nonnull
  private SearchContext getSearchContext() {
    return parent == null ? driver().getWebDriver() :
        (parent instanceof SelenideElement) ? ((SelenideElement) parent).toWebElement() :
        parent;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public ElementNotFound createElementNotFoundError(Condition condition, Throwable lastError) {
    if (parent instanceof SelenideElement) {
      ((SelenideElement) parent).should(exist);
    }
    else if (parent instanceof WebElement) {
      WebElementWrapper.wrap(driver(), (WebElement) parent).should(exist);
    }

    return super.createElementNotFoundError(condition, lastError);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getSearchCriteria() {
    return index == 0 ?
        describe.selector(criteria) :
        describe.selector(criteria) + '[' + index + ']';
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "{" + getSearchCriteria() + '}';
  }
}
