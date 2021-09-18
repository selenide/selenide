package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class BySelectorCollection implements CollectionSource {
  private final WebElementSelector elementSelector = inject(WebElementSelector.class);
  private final ElementDescriber describe = inject(ElementDescriber.class);

  private final Driver driver;
  private final WebElementSource parent;
  private final By selector;
  private Alias alias = NONE;

  public BySelectorCollection(Driver driver, By selector) {
    this(driver, null, selector);
  }

  public BySelectorCollection(Driver driver, @Nullable WebElementSource parent, By selector) {
    this.driver = driver;
    this.parent = parent;
    this.selector = selector;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    return elementSelector.findElements(driver, parent, selector);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    return elementSelector.findElement(driver, parent, selector, index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String description() {
    return alias.getOrElse(this::composeDescription);
  }

  @Nonnull
  private String composeDescription() {
    return parent == null ? describe.selector(selector) :
      parent.getSearchCriteria() + "/" + describe.selector(selector);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return driver;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }
}
