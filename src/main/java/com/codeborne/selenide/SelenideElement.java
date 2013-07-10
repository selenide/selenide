package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.*;

import java.io.File;

public interface SelenideElement extends WebElement, FindsByLinkText, FindsById, FindsByName,
    FindsByTagName, FindsByClassName, FindsByCssSelector,
    FindsByXPath, WrapsDriver, Locatable {

  /**
   * <p>
   * Actual for &lt;a href&gt; elements. Navigates browser by clicking this link.
   * </p>
   *
   * <p>
   * In some situations just clicking is not enough: $.click() doesn't take effect for &lt;a href&gt;.
   * In these cases use #followLink that actually navigates browser to @href value.
   * </p>
   */
  void followLink();

  /**
   * <p>Clear the text field, enter given text and trigger "change" event.</p>
   * <p>
   * Implementation details: this is the same as <pre>
   *   1. WebElement.clear()
   *   2. WebElement.sendKeys(text)
   *   3. Trigger change event</pre>
   * </p>
   *
   * @param text Any text to enter into the text field.
   */
  SelenideElement setValue(String text);

  /**
   * Same as #type(java.lang.String)
   */
  SelenideElement val(String text);

  /**
   * <p>Append given test to the text field and trigger "change" event.</p>
   *
   * <p>
   * Implementation details:
   * This is the same as <pre>
   *   1. WebElement.sendKeys(text)
   *   2. Trigger change event</pre>
   * </p>
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

  /**
   * Press TAB. Useful for input field and textareas: <pre>
   *  $("#to").val("stiven@seagal.com").pressTab();</pre>
   *
   * Implementation details:
   * This is the same as <pre>
   *  WebElement.sendKeys(Keys.TAB)</pre>
   */
  SelenideElement pressTab();

  /**
   * Short form of getText()
   * @see org.openqa.selenium.WebElement#getText()
   */
  String text();

  /**
   * Get the "value" attribute of the element
   * @return attribute "value" value or empty string if attribute is missing
   */
  String val();

  /**
   * Get value of attribute "data-<code>dataAttributeName</code>"
   */
	String data(String dataAttributeName);

  /**
   * Checks if element exists true on the current page.
   * @return false if element is not found, browser is closed or any WebDriver exception happened
   */
  boolean exists();

  /**
   * Check if this element exists and visible.
   * @return false if element does not exists, is invisible, browser is closed or any WebDriver exception happened.
   */
  @Override
  boolean isDisplayed();

  /**
   * <p>Checks that given element meets all of given conditions.</p>
   *
   * <p>
   * IMPORTANT: If element does not match then conditions immediately, waits up to
   * 4 seconds until element meets the conditions. It's extremely useful for dynamic content.
   * </p>
   *
   * <p>Timeout is configurable via Configuration#timeout</p>
   *
   * <p>For example: <code>
   *   $("#errorMessage").should(appear);
   * </code></p>
   *
   * @return Given element, useful for chaining:
   * <code>$("#errorMessage").should(appear).shouldBe(enabled);</code>
   *
   * @see Configuration#timeout
   */
  SelenideElement should(Condition... condition);

  /**
   * <p>Synonym for #should. Useful for better readability.</p>
   * <p>For example: <code>
   *   $("#errorMessage").shouldHave(text("Hello"), text("World"));
   * </code></p>

   * @see SelenideElement#should(com.codeborne.selenide.Condition...)
   */
  SelenideElement shouldHave(Condition... condition);

  /**
   * <p>Synonym for #should. Useful for better readability.</p>
   * <p>For example: <code>
   *   $("#errorMessage").shouldBe(visible, enabled);
   * </code></p>
   *
   * @see SelenideElement#should(com.codeborne.selenide.Condition...)
   */
  SelenideElement shouldBe(Condition... condition);

  /**
   * <p>Checks that given element does not meet given conditions.</p>
   *
   * <p>
   * IMPORTANT: If element does match the conditions, waits up to
   * 4 seconds until element does not meet the conditions. It's extremely useful for dynamic content.
   * </p>
   *
   * <p>Timeout is configurable via Configuration#timeout</p>
   *
   * <p>For example: <code>
   *   $("#errorMessage").should(exist);
   * </code></p>
   *
   * @see Configuration#timeout
   */
  SelenideElement shouldNot(Condition... condition);

  /**
   * <p>Synonym for #shouldNot. Useful for better readability.</p>
   * <p>For example: <code>
   *   $("#errorMessage").shouldNotHave(text("Exception"), text("Error"));
   * </code></p>
   *
   * @see SelenideElement#shouldNot(com.codeborne.selenide.Condition...)
   */
  SelenideElement shouldNotHave(Condition... condition);

  /**
   * <p>Synonym for #shouldNot. Useful for better readability.</p>
   * <p>For example: <code>
   *   $("#errorMessage").shouldNotBe(visible, enabled);
   * </code></p>
   *
   * @see SelenideElement#shouldNot(com.codeborne.selenide.Condition...)
   */
  SelenideElement shouldNotBe(Condition... condition);

  /**
   * <p>Wait until given element meets given conditions.</p>
   *
   * <p>IMPORTANT: in most cases you don't need this method because all should- methods wait too.
   * You need to use #waitUntil or #waitFor methods only if you need another timeout.</p>
   *
   * @param condition e.g. enabled, visible, text() and so on
   * @param timeoutMilliseconds timeout in milliseconds.
   */
  SelenideElement waitUntil(Condition condition, long timeoutMilliseconds);

  /**
   * <p>Wait until given element does not meet given conditions.</p>
   *
   * <p>IMPORTANT: in most cases you don't need this method because all shouldNot- methods wait too.
   * You need to use #waitUntil or #waitFor methods only if you need another timeout.</p>
   *
   * @param condition e.g. enabled, visible, text() and so on
   * @param timeoutMilliseconds timeout in milliseconds.
   */
  SelenideElement waitWhile(Condition condition, long timeoutMilliseconds);

  /**
   * Displays WebElement in human-readable format.
   * Useful for logging and debugging.
   * Not recommended to use for test verifications.
   *
   * @return e.g. <strong id=orderConfirmedStatus class=>Order has been confirmed</strong>
   */
  @Override String toString();

  /**
   * <p>Find the first matching element inside given element</p>
   * <p>Short form of <code>webElement.findElement(By.cssSelector(cssSelector))</code></p>
   */
  SelenideElement find(String cssSelector);

  /**
   * <p>Find the Nth matching element inside given element</p>
   */
  SelenideElement find(String cssSelector, int index);

  /**
   * com.codeborne.selenide.SelenideElement#find(java.lang.String)
   */
  SelenideElement find(By selector);

  /**
   * com.codeborne.selenide.SelenideElement#find(java.lang.String)
   */
  SelenideElement find(By selector, int index);

  /**
   * @see SelenideElement#find(java.lang.String)
   */
  SelenideElement $(String cssSelector);

  /**
   * com.codeborne.selenide.SelenideElement#find(java.lang.String)
   */
  SelenideElement $(String cssSelector, int index);

  /**
   * com.codeborne.selenide.SelenideElement#find(java.lang.String)
   */
  SelenideElement $(By selector);

  /**
   * com.codeborne.selenide.SelenideElement#find(java.lang.String)
   */
  SelenideElement $(By selector, int index);

  /**
   * <p>
   * Short form of <code>webDriver.findElements(thisElement, By.cssSelector(cssSelector))</code>
   * </p>
   *
   * <p>
   * For example, <code>$("#multirowTable").findAll("tr.active").shouldHave(size(2));</code>
   * </p>
   *
   * @return list of elements inside given element matching given CSS selector
   */
  ElementsCollection findAll(String cssSelector);

  /**
   * <p>
   * Short form of <code>webDriver.findElements(thisElement, selector)</code>
   * </p>
   *
   * <p>
   * For example, <code>$("#multirowTable").findAll(By.className("active")).shouldHave(size(2));</code>
   * </p>

   * @return list of elements inside given element matching given criteria
   */
  ElementsCollection findAll(By selector);

  /**
   * com.codeborne.selenide.SelenideElement#findAll(java.lang.String)
   */
  ElementsCollection $$(String cssSelector);

  /**
   * com.codeborne.selenide.SelenideElement#findAll(java.lang.String)
   */
  ElementsCollection $$(By selector);

  /**
   * <p>Upload file into file upload field. File is searched from classpath.</p>
   * <p>Short form of <code>webElement.sendKeys("c:/files/my-file.txt");</code></p>
   */
  File uploadFromClasspath(String fileName);

  /**
   * Select an option from dropdown list
   * @param text visible text of option
   */
  void selectOption(String text);

  /**
   * Select an option by value from dropdown list
   * @param value "value" attribute of option
   */
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
   * @return the original Selenium WebElement wrapped by this object
   */
  WebElement toWebElement();
}
