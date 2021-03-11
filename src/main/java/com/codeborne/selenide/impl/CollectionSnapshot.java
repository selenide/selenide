package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.impl.Alias.NONE;

@ParametersAreNonnullByDefault
public class CollectionSnapshot implements CollectionSource {

  private final CollectionSource originalCollection;
  private final List<WebElement> elementsSnapshot;
  private Alias alias = NONE;

  public CollectionSnapshot(CollectionSource collection) {
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
    return alias.getOrElse(() ->
      String.format("%s.snapshot(%d elements)", originalCollection.description(), elementsSnapshot.size())
    );
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
