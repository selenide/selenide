package com.codeborne.selenide;

import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collection;

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

  /**
   * Lets a subclass produce elements of a more specific class than {@link SelenideElement},
   * e.g. a plugin module's own element interface, while keeping this class' own generic parameter
   * (and therefore the declared return type of {@code get()}/{@code first()}/{@code last()}/{@code find()}/
   * {@code findBy()}/{@code iterator()}/{@code stream()}) unchanged.
   */
  protected ElementsCollection(CollectionSource collection, Class<? extends SelenideElement> elementClass) {
    super(collection, elementClass);
  }

  @Override
  protected ElementsCollection create(CollectionSource source) {
    return new ElementsCollection(source);
  }
}
