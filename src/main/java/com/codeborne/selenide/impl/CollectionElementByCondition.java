package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;

public class CollectionElementByCondition extends WebElementSource {

  public static SelenideElement wrap(CollectionSource collection, WebElementCondition condition) {
    return (SelenideElement) Proxy.newProxyInstance(
      collection.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
      new SelenideElementProxy<>(new CollectionElementByCondition(collection, condition)));
  }

  private final CollectionSource collection;
  private final WebElementCondition condition;

  CollectionElementByCondition(CollectionSource collection, WebElementCondition condition) {
    this.collection = collection;
    this.condition = condition;
  }

  @Override
  public Driver driver() {
    return collection.driver();
  }

  @Override
  public WebElement getWebElement() {
    List<WebElement> list = collection.getElements();

    for (WebElement element : list) {
      if (condition.check(driver(), element).verdict() == ACCEPT) {
        return element;
      }
    }

    throw new NoSuchElementException("Cannot locate an element " + description());
  }

  @Override
  public String getSearchCriteria() {
    return collection.getSearchCriteria() + ".findBy(" + condition + ")";
  }

  @Override
  public String description() {
    return getAlias().getOrElse(() -> collection.shortDescription() + ".findBy(" + condition + ")");
  }
}
