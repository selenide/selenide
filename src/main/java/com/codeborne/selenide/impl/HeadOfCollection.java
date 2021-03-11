package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

@ParametersAreNonnullByDefault
public class HeadOfCollection implements CollectionSource {
  private final CollectionSource originalCollection;
  private final int size;
  private Alias alias = NONE;

  public HeadOfCollection(CollectionSource originalCollection, int size) {
    this.originalCollection = originalCollection;
    this.size = size;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return originalCollection.driver();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    List<WebElement> source = originalCollection.getElements();
    return source.subList(0, Math.min(source.size(), size));
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
    }
    return originalCollection.getElement(index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String description() {
    return alias.getOrElse(() -> originalCollection.description() + ":first(" + size + ')');
  }

  @Override
  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }
}
