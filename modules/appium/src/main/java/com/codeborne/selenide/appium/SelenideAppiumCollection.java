package com.codeborne.selenide.appium;

import com.codeborne.selenide.BaseElementsCollection;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collection;

public class SelenideAppiumCollection extends BaseElementsCollection<SelenideAppiumElement, SelenideAppiumCollection> {
  SelenideAppiumCollection(CollectionSource collection) {
    super(collection);
  }

  SelenideAppiumCollection(Driver driver, Collection<? extends WebElement> elements) {
    super(driver, elements);
  }

  SelenideAppiumCollection(Driver driver, By seleniumSelector) {
    super(driver, seleniumSelector);
  }

  @Override
  protected SelenideAppiumCollection create(CollectionSource source) {
    return new SelenideAppiumCollection(source);
  }
}
