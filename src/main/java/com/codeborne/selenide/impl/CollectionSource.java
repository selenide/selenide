package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface CollectionSource {
  /**
   * get elements of this collection (probably cached).
   */
  List<WebElement> getElements();

  /**
   * get Nth element of this collection
   */
  WebElement getElement(int index);

  String getSearchCriteria();

  default String description() {
    return getAlias().get(() -> getSearchCriteria());
  }

  default String shortDescription() {
    return getAlias().getOrElse(() -> getSearchCriteria());
  }

  Driver driver();

  Alias getAlias();

  void setAlias(String alias);
}
