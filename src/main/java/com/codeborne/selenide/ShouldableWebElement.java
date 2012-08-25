package com.codeborne.selenide;

import org.openqa.selenium.WebElement;

public interface ShouldableWebElement extends WebElement {
  void should(Condition condition);
}
