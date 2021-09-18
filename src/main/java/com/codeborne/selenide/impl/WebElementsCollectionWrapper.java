package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

@ParametersAreNonnullByDefault
public class WebElementsCollectionWrapper implements CollectionSource {
  private final List<WebElement> elements;
  private final Driver driver;
  private Alias alias = NONE;

  public WebElementsCollectionWrapper(Driver driver, Collection<? extends WebElement> elements) {
    this.driver = driver;
    this.elements = new ArrayList<>(elements);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    return elements;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    return elements.get(index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String description() {
    return alias.getOrElse(() -> "$$(" + elements.size() + " elements)");
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
