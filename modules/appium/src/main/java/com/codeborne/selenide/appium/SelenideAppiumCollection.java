package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.WebElementsCollectionWrapper;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collection;

public class SelenideAppiumCollection extends ElementsCollection {
  SelenideAppiumCollection(CollectionSource collection) {
    super(collection, SelenideAppiumElement.class);
  }

  SelenideAppiumCollection(Driver driver, Collection<? extends WebElement> elements) {
    this(new WebElementsCollectionWrapper(driver, elements));
  }

  SelenideAppiumCollection(Driver driver, By seleniumSelector) {
    this(new BySelectorCollection(driver, seleniumSelector));
  }

  public SelenideAppiumCollection(WebElementSource parent, By selector) {
    this(new BySelectorCollection(parent.driver(), parent, selector));
  }

  @Override
  protected SelenideAppiumCollection create(CollectionSource source) {
    return new SelenideAppiumCollection(source);
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection should(WebElementsCondition... conditions) {
    return (SelenideAppiumCollection) super.should(conditions);
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection should(WebElementsCondition condition, Duration timeout) {
    return (SelenideAppiumCollection) super.should(condition, timeout);
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection shouldBe(WebElementsCondition... conditions) {
    return (SelenideAppiumCollection) super.shouldBe(conditions);
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection shouldBe(WebElementsCondition condition, Duration timeout) {
    return (SelenideAppiumCollection) super.shouldBe(condition, timeout);
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection shouldHave(WebElementsCondition... conditions) {
    return (SelenideAppiumCollection) super.shouldHave(conditions);
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection shouldHave(WebElementsCondition condition, Duration timeout) {
    return (SelenideAppiumCollection) super.shouldHave(condition, timeout);
  }

  @Override
  public SelenideAppiumCollection filter(WebElementCondition condition) {
    return (SelenideAppiumCollection) super.filter(condition);
  }

  @Override
  public SelenideAppiumCollection filterBy(WebElementCondition condition) {
    return filter(condition);
  }

  @Override
  public SelenideAppiumCollection exclude(WebElementCondition condition) {
    return (SelenideAppiumCollection) super.exclude(condition);
  }

  @Override
  public SelenideAppiumCollection excludeWith(WebElementCondition condition) {
    return exclude(condition);
  }

  @Override
  public SelenideAppiumCollection first(int elements) {
    return (SelenideAppiumCollection) super.first(elements);
  }

  @Override
  public SelenideAppiumCollection last(int elements) {
    return (SelenideAppiumCollection) super.last(elements);
  }

  @Override
  public SelenideAppiumCollection snapshot() {
    return (SelenideAppiumCollection) super.snapshot();
  }

  @Override
  @CanIgnoreReturnValue
  public SelenideAppiumCollection as(String alias) {
    return (SelenideAppiumCollection) super.as(alias);
  }

  @Override
  public SelenideAppiumElement get(int index) {
    return (SelenideAppiumElement) super.get(index);
  }

  @Override
  public SelenideAppiumElement first() {
    return (SelenideAppiumElement) super.first();
  }

  @Override
  public SelenideAppiumElement last() {
    return (SelenideAppiumElement) super.last();
  }

  @Override
  public SelenideAppiumElement find(WebElementCondition condition) {
    return (SelenideAppiumElement) super.find(condition);
  }

  @Override
  public SelenideAppiumElement findBy(WebElementCondition condition) {
    return find(condition);
  }
}
