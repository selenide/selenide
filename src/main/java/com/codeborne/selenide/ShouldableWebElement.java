package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;

public interface ShouldableWebElement extends WebElement {
  /**
   * Clear the text field and enter given text.
   * @param text Any text to enter into the text field.
   */
  ShouldableWebElement setValue(String text);

  /**
   * Same as #type(java.lang.String)
   */
  ShouldableWebElement val(String text);

  /**
   * Press ENTER. Useful for input field and textareas:
   * $("query").val("Aikido techniques").pressEnter();
   */
  ShouldableWebElement pressEnter();

  String text();

  /**
   * Get the "value" attribute of the element
   * @return attribute "value" value or empty string if attribute is missing
   */
  String val();

  boolean exists();

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
  ShouldableWebElement find(String cssSelector, int index);
  ShouldableWebElement find(By selector);
  ShouldableWebElement find(By selector, int index);

  File uploadFromClasspath(String fileName);

  void selectOption(String text);
  void selectOptionByValue(String value);
}
