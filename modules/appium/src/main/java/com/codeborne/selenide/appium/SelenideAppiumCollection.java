package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.CollectionElement;
import com.codeborne.selenide.impl.CollectionElementByCondition;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.LastCollectionElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.WebElementsCollectionWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collection;

public class SelenideAppiumCollection extends ElementsCollection {
  private final CollectionSource source;

  SelenideAppiumCollection(CollectionSource collection) {
    super(collection);
    this.source = collection;
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
    return (SelenideAppiumCollection) super.filterBy(condition);
  }

  @Override
  public SelenideAppiumCollection exclude(WebElementCondition condition) {
    return (SelenideAppiumCollection) super.exclude(condition);
  }

  @Override
  public SelenideAppiumCollection excludeWith(WebElementCondition condition) {
    return (SelenideAppiumCollection) super.excludeWith(condition);
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
    return CollectionElement.wrap(SelenideAppiumElement.class, source, index);
  }

  @Override
  public SelenideAppiumElement first() {
    return get(0);
  }

  @Override
  public SelenideAppiumElement last() {
    return LastCollectionElement.wrap(source, SelenideAppiumElement.class);
  }

  @Override
  public SelenideAppiumElement find(WebElementCondition condition) {
    return CollectionElementByCondition.wrap(source, condition, SelenideAppiumElement.class);
  }

  @Override
  public SelenideAppiumElement findBy(WebElementCondition condition) {
    return find(condition);
  }
}
