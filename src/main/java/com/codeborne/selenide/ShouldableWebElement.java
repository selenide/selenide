package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface ShouldableWebElement extends WebElement {
  ShouldableWebElement should(Condition... condition);
  ShouldableWebElement shouldHave(Condition... condition);
  ShouldableWebElement shouldBe(Condition... condition);

  ShouldableWebElement shouldNot(Condition... condition);
  ShouldableWebElement shouldNotHave(Condition... condition);
  ShouldableWebElement shouldNotBe(Condition... condition);

  /**
   * Displays WebElement in human-readable format
   * @return e.g. <strong id=orderConfirmedStatus class=>Order has been confirmed</strong>
   */
  @Override String toString();

  ShouldableWebElement find(String cssSelector);
  ShouldableWebElement find(By selector);
}
