package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface WebElementsCollection {
  abstract List<WebElement> getActualElements();
}
