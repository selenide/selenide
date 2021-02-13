package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class SnapshotCollection implements CollectionSource {

  private final CollectionSource originalCollection;
  private final List<WebElement> elementsSnapshot;

  public SnapshotCollection(CollectionSource collection) {
    this.originalCollection = collection;
    this.elementsSnapshot = new ArrayList<>(collection.getElements());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> getElements() {
    return elementsSnapshot;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebElement getElement(int index) {
    return elementsSnapshot.get(index);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String description() {
    return String.format("%s.snapshot(%d elements)", originalCollection.description(), elementsSnapshot.size());
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Driver driver() {
    return originalCollection.driver();
  }
}
