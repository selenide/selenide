package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.lang.Thread.currentThread;

public class ElementFinder extends WebElementSource {
  private final WebElementSelector elementSelector = inject(WebElementSelector.class);
  private final ElementDescriber describe = inject(ElementDescriber.class);

  public static SelenideElement wrap(Driver driver, String cssSelector, int index) {
    return wrap(driver, null, By.cssSelector(cssSelector), index);
  }

  public static SelenideElement wrap(Driver driver, By criteria) {
    return wrap(driver, null, criteria, 0);
  }

  public static SelenideElement wrap(Driver driver, @Nullable WebElementSource parent, By criteria, int index) {
    return wrap(driver, SelenideElement.class, parent, criteria, index);
  }

  public static <T extends SelenideElement> T wrap(Driver driver,
                                                   Class<T> clazz,
                                                   @Nullable WebElementSource parent,
                                                   By criteria,
                                                   int index) {
    return wrap(driver, clazz, parent, criteria, index, null);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Driver driver,
                                                   Class<T> clazz,
                                                   @Nullable WebElementSource parent,
                                                   By criteria,
                                                   int index,
                                                   @Nullable String alias) {
    return (T) Proxy.newProxyInstance(
      currentThread().getContextClassLoader(),
      new Class<?>[]{clazz},
      new SelenideElementProxy<>(new ElementFinder(driver, parent, criteria, index, alias)));
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz,
                                                   WebElementSource element) {
    return (T) Proxy.newProxyInstance(
      currentThread().getContextClassLoader(),
      new Class<?>[]{clazz},
      new SelenideElementProxy<>(element));
  }

  private final Driver driver;
  @Nullable
  private final WebElementSource parent;
  private final By criteria;
  private final int index;

  ElementFinder(Driver driver, @Nullable WebElementSource parent, By criteria, int index) {
    this(driver, parent, criteria, index, null);
  }

  public ElementFinder(Driver driver, @Nullable WebElementSource parent, By criteria, int index, @Nullable String alias) {
    this.driver = driver;
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
    if (alias != null) setAlias(alias);
  }

  @Override
  public final void setAlias(String alias) {
    super.setAlias(alias);
  }

  @Override
  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    if (arg instanceof By by) {
      return wrap(driver, this, by, index);
    }
    if (arg instanceof String cssLocator) {
      return wrap(driver, this, By.cssSelector(cssLocator), index);
    }
    throw new IllegalArgumentException("Unsupported locator type: " + arg);
  }

  @Override
  public Driver driver() {
    return driver;
  }

  @Override
  public WebElement getWebElement() throws NoSuchElementException, IndexOutOfBoundsException {
    return elementSelector.findElement(driver, parent, criteria, index);
  }

  @Override
  public List<WebElement> findAll() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
      elementSelector.findElements(driver(), parent, criteria) :
      super.findAll();
  }

  @Override
  public ElementNotFound createElementNotFoundError(WebElementCondition condition, @Nullable Throwable cause) {
    if (parent != null) {
      parent.checkCondition("", exist, false);
    }

    return super.createElementNotFoundError(condition, cause);
  }

  @Override
  public String getSearchCriteria() {
    return parent == null ?
      elementCriteria() :
      parent.getSearchCriteria() + "/" + elementCriteria();
  }

  private String elementCriteria() {
    return index == 0 ?
      describe.selector(criteria) :
      describe.selector(criteria) + '[' + index + ']';
  }

  @Override
  public String toString() {
    return "{" + description() + '}';
  }
}
