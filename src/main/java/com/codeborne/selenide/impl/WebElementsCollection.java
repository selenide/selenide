package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface WebElementsCollection {
  /**
   * gets currently loaded collection (probably cached).
   * If collection is not loaded yet, calls #getActualElements().
   * If you need to load the freshest data (not cached), call #getActualElements().
   *
   * @see #getActualElements()
   */
  List<WebElement> getElements();

  /**
   * fetches the current collection state from the webdriver
   */
  List<WebElement> getActualElements();

  String description();
}
