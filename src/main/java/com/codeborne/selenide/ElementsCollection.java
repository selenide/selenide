package com.codeborne.selenide;

import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public class ElementsCollection extends BaseElementsCollection<SelenideElement, ElementsCollection> {
  public ElementsCollection(CollectionSource collection) {
    super(collection);
  }

  public ElementsCollection(Driver driver, Collection<? extends WebElement> elements) {
    super(driver, elements);
  }

  public ElementsCollection(Driver driver, String cssSelector) {
    super(driver, cssSelector);
  }

  public ElementsCollection(Driver driver, By seleniumSelector) {
    super(driver, seleniumSelector);
  }

  @Override
  protected ElementsCollection create(CollectionSource source) {
    return new ElementsCollection(source);
  }
}
