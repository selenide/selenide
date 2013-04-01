package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.*;

import java.io.File;

public interface SelenideElement extends WebElement, FindsByLinkText, FindsById, FindsByName,
    FindsByTagName, FindsByClassName, FindsByCssSelector,
    FindsByXPath, WrapsDriver, Locatable {

  /**
   * Actual for &lt;a href&gt; elements. Navigates browser by clicking this links.
   */
  void followLink();

  /**
   * Clear the text field and enter given text.
   *
   * Implementation details:
   * This is the same as <pre>
   *   1. WebElement.clear()
   *   2. WebElement.sendKeys(text)</pre>
   *
   * @param text Any text to enter into the text field.
   */
  SelenideElement setValue(String text);

  /**
   * Same as #type(java.lang.String)
   */
  SelenideElement val(String text);

  /**
   * Append given test to the text field
   *
   * Implementation details:
   * This is the same as <pre>
   *  WebElement.sendKeys(text)</pre>
   *
   * @param text Any text to append into the text field.
   */
  SelenideElement append(String text);

  /**
   * Press ENTER. Useful for input field and textareas: <pre>
   *  $("query").val("Aikido techniques").pressEnter();</pre>
   *
   * Implementation details:
   * This is the same as <pre>
   *  WebElement.sendKeys(Keys.ENTER)</pre>
   */
  SelenideElement pressEnter();

  String text();

  /**
   * Get the "value" attribute of the element
   * @return attribute "value" value or empty string if attribute is missing
   */
  String val();

  /**
   * Get value of attribute "data-<dataAttributeName>"
   */
	String data(String dataAttributeName);

  boolean exists();

  /**
   * Check if this element exists and visible.
   * @return false if element does not exists, or it not displayed
   */
  @Override
  boolean isDisplayed();

  SelenideElement should(Condition... condition);
  SelenideElement shouldHave(Condition... condition);
  SelenideElement shouldBe(Condition... condition);

  SelenideElement shouldNot(Condition... condition);
  SelenideElement shouldNotHave(Condition... condition);
  SelenideElement shouldNotBe(Condition... condition);

  SelenideElement waitUntil(Condition condition, long timeoutMilliseconds);
  SelenideElement waitWhile(Condition condition, long timeoutMilliseconds);

  /**
   * Displays WebElement in human-readable format
   * @return e.g. <strong id=orderConfirmedStatus class=>Order has been confirmed</strong>
   */
  @Override String toString();

  SelenideElement find(String cssSelector);
  SelenideElement find(String cssSelector, int index);
  SelenideElement find(By selector);
  SelenideElement find(By selector, int index);

  /**
   * Dollar methods are synonym for "find" methods
   */
  SelenideElement $(String cssSelector);
  SelenideElement $(String cssSelector, int index);
  SelenideElement $(By selector);
  SelenideElement $(By selector, int index);

  ElementsCollection findAll(String cssSelector);
  ElementsCollection findAll(By selector);

  /**
   * Double-dollar methods are synonyms for "findAll" methods
   */
  ElementsCollection $$(String cssSelector);
  ElementsCollection $$(By selector);

  File uploadFromClasspath(String fileName);

  void selectOption(String text);
  void selectOptionByValue(String value);

  /**
   * Find selected option from this select field
   * @return WebElement for selected &lt;option&gt; element
   * @throws org.openqa.selenium.NoSuchElementException if no options are selected
   */
  SelenideElement getSelectedOption();

  /**
   * Get value of selected option in select field
   */
  String getSelectedValue();

  /**
   * Get text of selected option in select field
   */
  String getSelectedText();

  /**
   * @return the original Selenium WebElement that is current object wrapper for
   */
  WebElement toWebElement();
}
