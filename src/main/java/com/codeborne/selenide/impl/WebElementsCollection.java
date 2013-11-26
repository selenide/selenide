package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface WebElementsCollection {
  List<WebElement> getActualElements();
  String description();
}
