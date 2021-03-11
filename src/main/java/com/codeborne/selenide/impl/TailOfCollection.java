package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

@ParametersAreNonnullByDefault
public class TailOfCollection implements CollectionSource {
  private final CollectionSource originalCollection;
  private final int size;
  private Alias alias = NONE;

  public TailOfCollection(CollectionSource originalCollection, int size) {
    this.originalCollection = originalCollection;
    this.size = size;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    List<WebElement> source = originalCollection.getElements();
    int sourceCollectionSize = source.size();
    return source.subList(startingIndex(sourceCollectionSize), sourceCollectionSize);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    List<WebElement> source = originalCollection.getElements();
    int sourceCollectionSize = source.size();
    int startingIndex = startingIndex(sourceCollectionSize);
    return originalCollection.getElement(startingIndex + index);
  }

  private int startingIndex(int sourceCollectionSize) {
    return sourceCollectionSize - Math.min(sourceCollectionSize, this.size);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String description() {
    return alias.getOrElse(() -> originalCollection.description() + ":last(" + size + ')');
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return originalCollection.driver();
  }

  @Override
  public void setAlias(String alias) {
    this.alias = new Alias(alias);
  }
}
