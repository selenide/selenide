package com.codeborne.selenide;

import org.openqa.selenium.WebElement;

public interface ShouldableWebElement extends WebElement {
  ShouldableWebElement should(Condition... condition);
  ShouldableWebElement shouldHave(Condition... condition);
  ShouldableWebElement shouldBe(Condition... condition);
}
