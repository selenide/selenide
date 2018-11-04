package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.interactions.internal.Locatable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Wrapper around {@link WebElement} with additional methods like
 * {@link #shouldBe(Condition...)} and {@link #shouldHave(Condition...)}
 */
public interface SelenideElement extends WebElement, WrapsDriver, WrapsElement, Locatable, TakesScreenshot {
  /**
   *
   * <b>Implementation details:</b>
   *
   * <p>If Configuration.versatileSetValue is true, can work as 'selectOptionByValue', 'selectRadio'</p>
   *
   * <p>If Configuration.fastSetValue is true, sets value by javascript instead of using Selenium built-in "sendKey" function
   * and trigger "focus", "keydown", "keypress", "input", "keyup", "change" events.
   *
   * <p>In other case behavior will be:
   * <pre>
   * 1. WebElement.clear()
   * 2. WebElement.sendKeys(text)
   * 3. Trigger change event
   * </pre>
   *
   * @param text Any text to enter into the text field or set by value for select/radio.
   *
   * @see com.codeborne.selenide.commands.SetValue
   */
  SelenideElement setValue(String text);

  /**
   * Same as #setValue(java.lang.String)
   * @see com.codeborne.selenide.commands.Val
   */
  SelenideElement val(String text);

  /**
   * Append given test to the text field and trigger "change" event.
   *
   * <p>
   * Implementation details:
   * This is the same as
   * <pre>
   *   1. WebElement.sendKeys(text)
   *   2. Trigger change event
   * </pre>
   * </p>
   *
   * @param text Any text to append into the text field.
   *
   * @see com.codeborne.selenide.commands.Append
   */
  SelenideElement append(String text);

  /**
   * Press ENTER. Useful for input field and textareas: <pre>
   *  $("query").val("Aikido techniques").pressEnter();</pre>
   *
   * Implementation details:
   * Check that element is displayed and execute <pre>
   *  WebElement.sendKeys(Keys.ENTER)</pre>
   *
   * @see com.codeborne.selenide.commands.PressEnter
   */
  SelenideElement pressEnter();

  /**
   * Press TAB. Useful for input field and textareas: <pre>
   *  $("#to").val("stiven@seagal.com").pressTab();</pre>
   *
   * Implementation details:
   * Check that element is displayed and execute <pre>
   *  WebElement.sendKeys(Keys.TAB)</pre>
   *
   * @see com.codeborne.selenide.commands.PressTab
   */
  SelenideElement pressTab();

  /**
   * Press ESCAPE. Useful for input field and textareas: <pre>
   *  $(".edit").click().pressEscape();</pre>
   *
   * Implementation details:
   * Check that element is displayed and execute <pre>
   *  WebElement.sendKeys(Keys.ESCAPE)</pre>
   *
   * @see com.codeborne.selenide.commands.PressEscape
   */
  SelenideElement pressEscape();

  /**
   * Get the visible text of this element, including sub-elements without leading/trailing whitespace.
   * NB! For "select", returns text(s) of selected option(s).
   *
   * @return The innerText of this element
   * @see com.codeborne.selenide.commands.GetText
   */
  @Override String getText();

  /**
   * Short form of getText()
   * @see WebElement#getText()
   * @see com.codeborne.selenide.commands.GetText
   */
  String text();

  /**
   * Get the text code of the element with children.
   *
   * Short form of getAttribute("textContent") or getAttribute("innerText") depending on browser.
   * @see com.codeborne.selenide.commands.GetInnerText
   */
  String innerText();

  /**
   * Get the HTML code of the element with children.
   *
   * Short form of getAttribute("innerHTML")
   * @see com.codeborne.selenide.commands.GetInnerHtml
   */
  String innerHtml();

  /**
   * Get the attribute of the element. Synonym for getAttribute(String).
   * @return null if attribute is missing
   * @see com.codeborne.selenide.commands.GetAttribute
   */
  String attr(String attributeName);

  /**
   * Get the "name" attribute of the element
   * @return attribute "name" value or null if attribute is missing
   * @see com.codeborne.selenide.commands.GetName
   */
  String name();

  /**
   * Get the "value" attribute of the element
   * Same as #getValue()
   * @return attribute "value" value or null if attribute is missing
   *
   * @see com.codeborne.selenide.commands.Val
   */
  String val();

  /**
   * Get the "value" attribute of the element
   * @return attribute "value" value or null if attribute is missing
   * @since 3.1
   *
   * @see com.codeborne.selenide.commands.GetValue
   */
  String getValue();

  /**
   * Select radio button
   * @param value value of radio button to select
   * @return selected "input type=radio" element
   *
   * @see com.codeborne.selenide.commands.SelectRadio
   */
  SelenideElement selectRadio(String value);

  /**
   * Get value of attribute "data-<i>dataAttributeName</i>"
   *
   * @see com.codeborne.selenide.commands.GetDataAttribute
   */
  String data(String dataAttributeName);

  /**
   * Checks if element exists true on the current page.
   * @return false if element is not found, browser is closed or any WebDriver exception happened
   *
   * @see com.codeborne.selenide.commands.Exists
   */
  boolean exists();

  /**
   * Check if this element exists and visible.
   * @return false if element does not exists, is invisible, browser is closed or any WebDriver exception happened.
   *
   * @see com.codeborne.selenide.commands.IsDisplayed
   */
  @Override
  boolean isDisplayed();

  /**
   * immediately returns true if element matches given condition
   * Method doesn't wait!
   * WARNING: This method can help implementing crooks, but it is not needed for typical ui tests.
   *
   * @see #has
   * @see com.codeborne.selenide.commands.Matches
   */
  boolean is(Condition condition);

  /**
   * immediately returns true if element matches given condition
   * Method doesn't wait!
   * WARNING: This method can help implementing crooks, but it is not needed for typical ui tests.
   *
   * @see #is
   * @see com.codeborne.selenide.commands.Matches
   */
  boolean has(Condition condition);

  /**
   * Set checkbox state to CHECKED or UNCHECKED.
   * @param selected true for checked and false for unchecked
   *
   * @see com.codeborne.selenide.commands.SetSelected
   */
  SelenideElement setSelected(boolean selected);

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
   * <p>For example: {@code
   *   $("#errorMessage").should(appear);
   * }</p>
   *
   * @return Given element, useful for chaining:
   * {@code $("#errorMessage").should(appear).shouldBe(enabled);}
   *
   * @see Configuration#timeout
   * @see com.codeborne.selenide.commands.Should
   */
  SelenideElement should(Condition... condition);

  /**
   * <p>Synonym for #should. Useful for better readability.</p>
   * <p>For example: {@code
   *   $("#errorMessage").shouldHave(text("Hello"), text("World"));
   * }</p>

   * @see SelenideElement#should(com.codeborne.selenide.Condition...)
   * @see com.codeborne.selenide.commands.ShouldHave
   */
  SelenideElement shouldHave(Condition... condition);

  /**
   * <p>Synonym for #should. Useful for better readability.</p>
   * <p>For example: {@code
   *   $("#errorMessage").shouldBe(visible, enabled);
   * }</p>
   *
   * @see SelenideElement#should(com.codeborne.selenide.Condition...)
   * @see com.codeborne.selenide.commands.ShouldBe
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
   * <p>For example: {@code
   *   $("#errorMessage").should(exist);
   * }</p>
   *
   * @see Configuration#timeout
   * @see com.codeborne.selenide.commands.ShouldNot
   */
  SelenideElement shouldNot(Condition... condition);

  /**
   * <p>Synonym for #shouldNot. Useful for better readability.</p>
   * <p>For example: {@code
   *   $("#errorMessage").shouldNotHave(text("Exception"), text("Error"));
   * }</p>
   *
   * @see SelenideElement#shouldNot(com.codeborne.selenide.Condition...)
   * @see com.codeborne.selenide.commands.ShouldNotHave
   */
  SelenideElement shouldNotHave(Condition... condition);

  /**
   * <p>Synonym for #shouldNot. Useful for better readability.</p>
   * <p>For example: {@code
   *   $("#errorMessage").shouldNotBe(visible, enabled);
   * }</p>
   *
   * @see SelenideElement#shouldNot(com.codeborne.selenide.Condition...)
   * @see com.codeborne.selenide.commands.ShouldNotBe
   */
  SelenideElement shouldNotBe(Condition... condition);

  /**
   * <p>Wait until given element meets given conditions.</p>
   *
   * <p>IMPORTANT: in most cases you don't need this method because all should- methods wait too.
   * You need to use #waitUntil or #waitWhile methods only if you need another timeout.</p>
   *
   * @param condition e.g. enabled, visible, text() and so on
   * @param timeoutMilliseconds timeout in milliseconds.
   * @see com.codeborne.selenide.commands.ShouldBe
   */
  SelenideElement waitUntil(Condition condition, long timeoutMilliseconds);

  /**
   * <p>Wait until given element meets given conditions.</p>
   *
   * <p>IMPORTANT: in most cases you don't need this method because all should- methods wait too.
   * You need to use #waitUntil or #waitWhile methods only if you need another timeout.</p>
   *
   * @param condition e.g. enabled, visible, text() and so on
   * @param timeoutMilliseconds timeout in milliseconds.
   * @param pollingIntervalMilliseconds  interval in milliseconds, when checking condition
   *
   * @see com.codeborne.selenide.commands.ShouldBe
   */
  SelenideElement waitUntil(Condition condition, long timeoutMilliseconds, long pollingIntervalMilliseconds);

  /**
   * <p>Wait until given element does not meet given conditions.</p>
   *
   * <p>IMPORTANT: in most cases you don't need this method because all shouldNot- methods wait too.
   * You need to use #waitUntil or #waitWhile methods only if you need another timeout.</p>
   *
   * @param condition e.g. enabled, visible, text() and so on
   * @param timeoutMilliseconds timeout in milliseconds.
   *
   * @see com.codeborne.selenide.commands.ShouldNotBe
   */
  SelenideElement waitWhile(Condition condition, long timeoutMilliseconds);

  /**
   * <p>Wait until given element does not meet given conditions.</p>
   *
   * <p>IMPORTANT: in most cases you don't need this method because all shouldNot- methods wait too.
   * You need to use #waitUntil or #waitWhile methods only if you need another timeout.</p>
   *
   * @param condition e.g. enabled, visible, text() and so on
   * @param timeoutMilliseconds timeout in milliseconds.
   * @param pollingIntervalMilliseconds  interval in milliseconds, when checking condition
   *
   * @see com.codeborne.selenide.commands.ShouldNotBe
   */
  SelenideElement waitWhile(Condition condition, long timeoutMilliseconds, long pollingIntervalMilliseconds);

  /**
   * Displays WebElement in human-readable format.
   * Useful for logging and debugging.
   * Not recommended to use for test verifications.
   *
   * @return e.g. <strong id=orderConfirmedStatus class=>Order has been confirmed</strong>
   *
   * @see com.codeborne.selenide.commands.ToString
   */
  @Override String toString();

  /**
   * Get parent element of this element
   * ATTENTION! This method doesn't start any search yet!
   * For example, $("td").parent() could give some "tr".
   * @return Parent element
   *
   * @see com.codeborne.selenide.commands.GetParent
   */
  SelenideElement parent();

  /**
   * Get last child element of this element
   * ATTENTION! this method doesn't start any search yet!
   * For example, $("tr").lastChild(); could give the last "td".
   */
  SelenideElement lastChild();

  /**
   * Locates closes ancestor element matching given criteria
   * ATTENTION! This method doesn't start any search yet!
   * For example, $("td").closest("table") could give some "table".
   *
   * @param tagOrClass Either HTML tag or CSS class. E.g. "form" or ".active".
   * @return Matching ancestor element
   *
   * @see com.codeborne.selenide.commands.GetClosest
   */
  SelenideElement closest(String tagOrClass);

  /**
   * <p>Locates the first matching element inside given element</p>
   * ATTENTION! This method doesn't start any search yet!
   * <p>Short form of {@code webElement.findElement(By.cssSelector(cssSelector))}</p>
   *
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement find(String cssSelector);

  /**
   * <p>Locates the Nth matching element inside given element</p>
   * ATTENTION! This method doesn't start any search yet!
   *
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement find(String cssSelector, int index);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #find(String)}
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement find(By selector);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #find(String, int)}
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement find(By selector, int index);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #find(String)}
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement $(String cssSelector);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #find(String, int)}
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement $(String cssSelector, int index);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #find(String)}
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement $(By selector);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #find(String, int)}
   * @see com.codeborne.selenide.commands.Find
   */
  SelenideElement $(By selector, int index);

  /**
   * <p>Locates the first matching element inside given element using xpath locator</p>
   * ATTENTION! This method doesn't start any search yet!
   * <p>Short form of {@code webElement.findElement(By.xpath(xpathLocator))}</p>
   *
   * @see com.codeborne.selenide.commands.FindByXpath
   */
  SelenideElement $x(String xpath);

  /**
   * <p>Locates the Nth matching element inside given element using xpath locator</p>
   * ATTENTION! This method doesn't start any search yet!
   *
   * @see com.codeborne.selenide.commands.FindByXpath
   */
  SelenideElement $x(String xpath, int index);

  /**
   * <p>
   * Short form of {@code webDriver.findElements(thisElement, By.cssSelector(cssSelector))}
   * </p>
   * ATTENTION! This method doesn't start any search yet!
   * <p>
   * For example, {@code $("#multirowTable").findAll("tr.active").shouldHave(size(2));}
   * </p>
   *
   * @return list of elements inside given element matching given CSS selector
   *
   * @see com.codeborne.selenide.commands.FindAll
   */
  ElementsCollection findAll(String cssSelector);

  /**
   * <p>
   * Short form of {@code webDriver.findElements(thisElement, selector)}
   * </p>
   * ATTENTION! This method doesn't start any search yet!
   * <p>
   * For example, {@code $("#multirowTable").findAll(By.className("active")).shouldHave(size(2));}
   * </p>
   *
   * @return list of elements inside given element matching given criteria
   *
   * @see com.codeborne.selenide.commands.FindAll
   */
  ElementsCollection findAll(By selector);

  /**
   * ATTENTION! This method doesn't start any search yet!
   * Same as {@link #findAll(String)}
   */
  ElementsCollection $$(String cssSelector);

  /**
   * Same as {@link #findAll(By)}
   */
  ElementsCollection $$(By selector);

  /**
   * <p>
   * Short form of {@code webDriver.findElements(thisElement, By.xpath(xpath))}
   * </p>
   * ATTENTION! This method doesn't start any search yet!
   * <p>
   * For example, {@code $("#multirowTable").$$x("./input").shouldHave(size(2));}
   * </p>
   *
   * @return list of elements inside given element matching given xpath locator
   *
   * @see com.codeborne.selenide.commands.FindAllByXpath
   */
  ElementsCollection $$x(String xpath);

  /**
   * <p>Upload file into file upload field. File is searched from classpath.</p>
   *
   * <p>Multiple file upload is also supported. Just pass as many file names as you wish.</p>
   * @param fileName name of the file or the relative path in classpath e.g. "files/1.pfd"
   * @return the object of the first file uploaded
   * @throws IllegalArgumentException if any of the files is not found
   *
   * @see com.codeborne.selenide.commands.UploadFileFromClasspath
   */
  File uploadFromClasspath(String... fileName);

  /**
   * <p>Upload file into file upload field.</p>
   *
   * <p>Multiple file upload is also supported. Just pass as many files as you wish.</p>
   * @param file file object(s)
   * @return the object of the first file uploaded
   * @throws IllegalArgumentException if any of the files is not found, or other errors
   *
   * @see com.codeborne.selenide.commands.UploadFile
   */
  File uploadFile(File... file);

  /**
   * Select an option from dropdown list (by index)
   * @param index 0..N (0 means first option)
   *
   * @see com.codeborne.selenide.commands.SelectOptionByTextOrIndex
   */
  void selectOption(int... index);

  /**
   * Select an option from dropdown list (by text)
   * @param text visible text of option
   *
   * @see com.codeborne.selenide.commands.SelectOptionByTextOrIndex
   */
  void selectOption(String... text);


  /**
   * Select an option from dropdown list that contains given text
   * @param text substring of visible text of option
   *
   * @see com.codeborne.selenide.commands.SelectOptionContainingText
   */
  void selectOptionContainingText(String text);

  /**
   * Select an option from dropdown list (by value)
   * @param value "value" attribute of option
   *
   * @see com.codeborne.selenide.commands.SelectOptionByValue
   */
  void selectOptionByValue(String... value);

  /**
   * Find (first) selected option from this select field
   * @return WebElement for selected &lt;option&gt; element
   * @throws NoSuchElementException if no options are selected
   *
   * @see com.codeborne.selenide.commands.GetSelectedOption
   */
  SelenideElement getSelectedOption() throws NoSuchElementException;

  /**
   * Find all selected options from this select field
   *
   * @return ElementsCollection for selected &lt;option&gt; elements (empty list if no options are selected)
   * @see com.codeborne.selenide.commands.GetSelectedOptions
   */
  ElementsCollection getSelectedOptions();

  /**
   * Get value of selected option in select field
   *
   * @see com.codeborne.selenide.commands.GetSelectedValue
   */
  String getSelectedValue();

  /**
   * Get text of selected option in select field
   *
   * @see com.codeborne.selenide.commands.GetSelectedText
   */
  String getSelectedText();

  /**
   * Ask browser to scroll to this element
   *
   * @see com.codeborne.selenide.commands.ScrollTo
   */
  SelenideElement scrollTo();

  /**
   * Ask browser to scrolls the element on which it's called into the visible area of the browser window.
   * <p>
   * If <b>alignToTop</b> boolean value is <i>true</i> - the top of the element will be aligned to the top.
   * <p>
   * If <b>alignToTop</b> boolean value is <i>false</i> - the bottom of the element will be aligned to the bottom.
   * Usage:
   * <pre>
   *     element.scrollIntoView(true);
   *     // Corresponds to scrollIntoViewOptions: {block: "start", inline: "nearest"}
   *
   *     element.scrollIntoView(false);
   *     // Corresponds to scrollIntoViewOptions: {block: "end", inline: "nearest"}
   * </pre>
   *
   * @param alignToTop boolean value that indicate how element will be aligned to the visible area of the scrollable ancestor.
   *
   * @see com.codeborne.selenide.commands.ScrollIntoView
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView">Web API reference</a>
   */
  SelenideElement scrollIntoView(boolean alignToTop);

  /**
   * Ask browser to scrolls the element on which it's called into the visible area of the browser window.
   * <pre>
   * scrollIntoViewOptions:
   *  * behavior (optional) - Defines the transition animation
   *    1. auto (default)
   *    2. instant
   *    3. smooth
   *  * block (optional)
   *    1. start
   *    2. center (default)
   *    3. end
   *    4. nearest
   *  * inline
   *    1. start
   *    2. center
   *    3. end
   *    4. nearest (default)
   * </pre>
   * <p>
   * Usage:
   * <pre>
   *     element.scrollIntoView("{block: \"end\"}");
   *     element.scrollIntoView("{behavior: \"instant\", block: \"end\", inline: \"nearest\"}");
   * </pre>
   *
   * @param scrollIntoViewOptions is an object with the align properties: behavior, block and inline.
   *
   * @see com.codeborne.selenide.commands.ScrollIntoView
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView">Web API reference</a>
   */
  SelenideElement scrollIntoView(String scrollIntoViewOptions);

  /**
   * Download file linked by "href" attribute of this element
   * @throws RuntimeException if 50x status code was returned from server
   * @throws FileNotFoundException if 40x status code was returned from server
   *
   * @see com.codeborne.selenide.commands.DownloadFile
   */
  File download() throws FileNotFoundException;

  /**
   * Download file linked by "href" attribute of this element or any file to which this element redirects.
   *
   * @param timeout download operations timeout.
   *
   * @throws RuntimeException      if 50x status code was returned from server
   * @throws FileNotFoundException if 40x status code was returned from server
   *
   * @see com.codeborne.selenide.commands.DownloadFile
   */
  File download(long timeout) throws FileNotFoundException;

  /**
   * Return criteria by which this element is located
   * @return e.g. "#multirowTable.findBy(text 'INVALID-TEXT')/valid-selector"
   */
  String getSearchCriteria();

  /**
   * @return the original Selenium WebElement wrapped by this object
   *
   * @see com.codeborne.selenide.commands.ToWebElement
   */
  WebElement toWebElement();

  /**
   * @return Underlying {@link WebElement}
   * @see com.codeborne.selenide.commands.GetWrappedElement
   */
  @Override
  WebElement getWrappedElement();

  /**
   * Click the element
   *
   * <p>
   * By default it uses default Selenium method click.
   * </p>
   * <p>
   * But it uses JavaScript method to click if {@code com.codeborne.selenide.Configuration#clickViaJs} is defined.
   * It may be helpful for testing in Internet Explorer where native click doesn't always work correctly.
   * </p>
   *
   * @see com.codeborne.selenide.commands.Click
   */
  @Override void click();

  /**
   * Click the element with a relative offset from the upper left corner of the element
   */
  void click(int offsetX, int offsetY);

  /**
   * Click with right mouse button on this element
   * @return this element
   *
   * @see com.codeborne.selenide.commands.ContextClick
   */
  SelenideElement contextClick();

  /**
   * Double click the element
   * @return this element
   *
   * @see com.codeborne.selenide.commands.DoubleClick
   */
  SelenideElement doubleClick();

  /**
   * Emulate "mouseOver" event. In other words, move mouse cursor over this element (without clicking it).
   * @return this element
   *
   * @see com.codeborne.selenide.commands.Hover
   */
  SelenideElement hover();

  /**
   * Drag and drop this element to the target
   *
   * Before dropping, waits until target element gets visible.
   *
   * @param targetCssSelector CSS selector defining target element
   * @return this element
   *
   * @see com.codeborne.selenide.commands.DragAndDropTo
   */
  SelenideElement dragAndDropTo(String targetCssSelector);

  /**
   * Drag and drop this element to the target
   *
   * Before dropping, waits until target element gets visible.
   *
   * @param target target element
   * @return this element
   *
   * @see com.codeborne.selenide.commands.DragAndDropTo
   */
  SelenideElement dragAndDropTo(WebElement target);

  /**
   * Check if image is properly loaded.
   *
   * @throws IllegalArgumentException if argument is not an "img" element
   * @since 2.13
   *
   * @see com.codeborne.selenide.commands.IsImage
   */
  boolean isImage();

  /**
   * Take screenshot of this element
   * @return file with screenshot (*.png)
   *
   * @see com.codeborne.selenide.commands.TakeScreenshot
   */
  File screenshot();

  /**
   * Take screenshot of this element
   * @return buffered image with screenshot
   *
   * @see com.codeborne.selenide.commands.TakeScreenshotAsImage
   */
  BufferedImage screenshotAsImage();
}
