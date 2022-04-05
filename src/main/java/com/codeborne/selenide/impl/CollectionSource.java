package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface CollectionSource {
  /**
   * get elements of this collection (probably cached).
   */
  @CheckReturnValue
  @Nonnull
  List<WebElement> getElements();

  /**
   * get Nth element of this collection
   */
  @CheckReturnValue
  @Nonnull
  WebElement getElement(int index);

  @CheckReturnValue
  @Nonnull
  String getSearchCriteria();

  @CheckReturnValue
  @Nonnull
  default String description() {
    return getAlias().get(() -> getSearchCriteria());
  }

  @CheckReturnValue
  @Nonnull
  default String shortDescription() {
    return getAlias().getOrElse(() -> getSearchCriteria());
  }

  @CheckReturnValue
  @Nonnull
  Driver driver();

  @CheckReturnValue
  @Nonnull
  Alias getAlias();

  void setAlias(String alias);
}
