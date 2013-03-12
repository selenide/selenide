package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.JQuery.jQuery;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.lang.Thread.currentThread;

public class WaitingSelenideElement extends AbstractSelenideElement {
  public static SelenideElement wrap(SelenideElement parent, By criteria, int index) {
    return (SelenideElement) Proxy.newProxyInstance(
        currentThread().getContextClassLoader(),
        new Class<?>[]{SelenideElement.class},
        new WaitingSelenideElement(parent, criteria, index));
  }

  private final SelenideElement parent;
  private final By criteria;
  private final int index;

  WaitingSelenideElement(SelenideElement parent, By criteria, int index) {
    this.parent = parent;
    this.criteria = criteria;
    this.index = index;
  }

  @Override
  protected WebElement getDelegate() {
    return waitUntil(exist, timeout);
  }

  @Override
  protected SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return arg instanceof By ?
        wrap(proxy, (By) arg, index) :
        wrap(proxy, By.cssSelector((String) arg), index);
  }

  @Override
  protected void selectOptionByText(WebElement selectField, String optionText) {
    super.selectOptionByText(selectField, optionText);
    jQuery.change(criteria, index); // TODO Use parent also
  }

  @Override
  protected void selectOptionByValue(WebElement selectField, String optionValue) {
    super.selectOptionByValue(selectField, optionValue);
    jQuery.change(criteria, index); // TODO Use parent also
  }

  @Override
  protected WebElement getActualDelegate() throws NoSuchElementException, IndexOutOfBoundsException {
    return index == 0 ?
        getSearchContext().findElement(criteria) :
        getSearchContext().findElements(criteria).get(index);
  }

  private SearchContext getSearchContext() {
    return parent == null ? getWebDriver() : parent.toWebElement();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '{' + criteria +
        (parent == null ? "" : ", in: " + parent.toString()) +
        (index == 0 ? "" : ", index: " + index) +
        '}';
  }
}
