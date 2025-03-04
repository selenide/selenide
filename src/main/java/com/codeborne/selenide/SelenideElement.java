package com.codeborne.selenide;

import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.commands.GetSelectedOptionValue;
import com.codeborne.selenide.commands.ScrollIntoCenter;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.WebElementSource;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;

/**
 * Wrapper around {@link WebElement} with additional methods like
 * {@link #shouldBe(WebElementCondition...)} and {@link #shouldHave(WebElementCondition...)}
 */
public interface SelenideElement extends WebElement, WrapsDriver, WrapsElement, Locatable, TakesScreenshot {
  /**
   * Set value to given input element.<p>

   * <b>Applicable for:</b>: {@code <input>} and {@code <textarea>}.<p>

   * <b>Implementation details:</b><p>
   *
   * If Configuration.fastSetValue is true, sets value by javascript instead of using Selenium built-in "sendKey" function
   * and trigger "focus", "keydown", "keypress", "input", "keyup", "change" events.<p>
   *
   * In other case behavior will be:
   * <pre>
   * 1. WebElement.clear()
   * 2. WebElement.sendKeys(text)
   * 3. Trigger change event
   * </pre>
   *
   * @param text Any text to enter into the text field or set by value for select/radio.
   * @see com.codeborne.selenide.commands.SetValue
   */
  @CanIgnoreReturnValue
  SelenideElement setValue(@Nullable String text);

  /**
   * Same as {@link #setValue(java.lang.String)}
   *
   * @see com.codeborne.selenide.commands.Val
   */
  @CanIgnoreReturnValue
  SelenideElement val(@Nullable String text);

  /**
   * Same as {@link #setValue(SetValueOptions)}
   *
   * @see com.codeborne.selenide.commands.Val
   */
  @CanIgnoreReturnValue
  SelenideElement val(SetValueOptions options);

  /**
   * Similar to {@link #setValue(java.lang.String)}
   *
   * @see com.codeborne.selenide.commands.SetValue
   */
  @CanIgnoreReturnValue
  SelenideElement setValue(SetValueOptions options);

  /**
   * Mimic how real user would type in a text field
   * Useful to work with autosuggestion dropdown
   * @see com.codeborne.selenide.commands.Type
   */
  @CanIgnoreReturnValue
  SelenideElement type(CharSequence text);

  /**
   * Similar to {@link #type(java.lang.CharSequence)}
   * @param options parameters specifying how exactly you want to type the text
   * @see com.codeborne.selenide.commands.Type
   */
  @CanIgnoreReturnValue
  SelenideElement type(TypeOptions options);

  /**
   * Append given text to the text field and trigger "change" event.<p>
   *
   * <b>Applicable for:</b> {@code <input>} and {@code <textarea>}.<p>
   *
   * <b>Implementation details:</b><p>
   *
   * This is the same as
   * <pre>
   *   1. WebElement.sendKeys(text)
   *   2. Trigger change event
   * </pre>
   *
   * @param text Any text to append into the text field.
   * @see com.codeborne.selenide.commands.Append
   */
  @CanIgnoreReturnValue
  SelenideElement append(String text);

  /**
   * Append text from clipboard to the text field and trigger "change" event.
   *
   * @see Clipboard
   * @see com.codeborne.selenide.commands.Paste
   */
  @CanIgnoreReturnValue
  SelenideElement paste();

  /**
   * Clear the input field<p>
   *
   * Basically, it's the same as {@link WebElement#clear()}, but it works. :)
   * @see com.codeborne.selenide.commands.Clear
   */
  @CanIgnoreReturnValue
  @Override
  void clear();

  /**
   * Press ENTER. Useful for input field and textareas: <pre>
   *  $("query").val("Dear Santa").pressEnter();</pre>
   * <p>
   *
   * <b>Implementation details:</b>
   * Check that element is displayed and execute <pre>
   *  WebElement.sendKeys(Keys.ENTER)</pre>
   *
   * @see com.codeborne.selenide.commands.PressEnter
   */
  @CanIgnoreReturnValue
  SelenideElement pressEnter();

  /**
   * Press TAB. Useful for input field and text areas: <pre>
   *  $("#to").val("santa@claus.fi").pressTab();</pre>
   * <p>
   * <b>Implementation details:</b><p>
   * Check that element is displayed and execute <pre>
   *  WebElement.sendKeys(Keys.TAB)</pre>
   *
   * @see com.codeborne.selenide.commands.PressTab
   */
  @CanIgnoreReturnValue
  SelenideElement pressTab();

  /**
   * Remove focus from this element
   * @since 7.1.0
   *
   * @see com.codeborne.selenide.commands.Unfocus
   */
  @CanIgnoreReturnValue
  SelenideElement unfocus();

  /**
   * Press ESCAPE. Useful for input field and textareas: <pre>
   *  $(".edit").click().pressEscape();</pre>
   * <p>
   *
   * <b>Implementation details:</b><p>
   * Check that element is displayed and execute <pre>
   *  WebElement.sendKeys(Keys.ESCAPE)</pre>
   *
   * @see com.codeborne.selenide.commands.PressEscape
   */
  @CanIgnoreReturnValue
  SelenideElement pressEscape();

  /**
   * Press a Key(s) on Keyboard: <pre>
   *  $(".edit").press(Keys.DOWN).click();</pre>
   * <p>
   *
   * <b>Implementation details:</b><p>
   * Checks that element is displayed and executes <pre>
   *  WebElement.sendKeys(keysToPress)</pre>
   *
   * Compared to {@link WebElement#sendKeys(CharSequence...)}, this method is chainable.
   *
   * @see com.codeborne.selenide.commands.Press
   * @see Keys
   */
  @CanIgnoreReturnValue
  SelenideElement press(CharSequence... keysToPress);

  /**
   * Get the visible text of this element, including sub-elements without leading/trailing whitespace.
   * NB! For "select", returns text(s) of selected option(s).
   *
   * @return The innerText of this element
   * @see com.codeborne.selenide.commands.GetText
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @NonNull
  @Override
  String getText();

  /**
   * Element alias, which can be set with {@link #as(String text)}
   *
   * Usually you should not need this method, unless you are writing a custom reporting engine like Allure Reports.
   *
   * @return Alias of this element or null, if element alias is not set
   * @see com.codeborne.selenide.commands.GetAlias
   */
  @Nullable
  String getAlias();

  /**
   * Short form of {@link #getText()}
   *
   * @see WebElement#getText()
   * @see com.codeborne.selenide.commands.GetText
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String text();

  /**
   * Get the text of the element WITHOUT children.
   *
   * @see com.codeborne.selenide.commands.GetOwnText
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String getOwnText();

  /**
   * Get the text code of the element with children.<p>
   * It can be used to get the text of a hidden element.<p>
   *
   * Short form of {@code getAttribute("textContent")} or {@code getAttribute("innerText")} depending on browser.
   *
   * @see com.codeborne.selenide.commands.GetInnerText
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String innerText();

  /**
   * Get the HTML code of the element with children.<p>
   * It can be used to get the html of a hidden element.<p>
   * Short form of {@code getAttribute("innerHTML")}.
   *
   * @see com.codeborne.selenide.commands.GetInnerHtml
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String innerHtml();

  /**
   * Get the attribute of the element. Synonym for {@link #getAttribute(String)}
   *
   * @return null if attribute is missing
   * @see com.codeborne.selenide.commands.GetAttribute
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String attr(String attributeName);

  /**
   * Get the "name" attribute of the element
   *
   * @return attribute "name" value or null if attribute is missing
   * @see com.codeborne.selenide.commands.GetName
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String name();

  /**
   * Get the "value" attribute of the element
   * Same as {@link #getValue()}
   *
   * @return attribute "value" value or null if attribute is missing
   * @see com.codeborne.selenide.commands.Val
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String val();

  /**
   * Get the "value" attribute of the element
   *
   * @return attribute "value" value or null if attribute is missing
   * @see com.codeborne.selenide.commands.GetValue
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String getValue();

  /**
   * Get the property value of the pseudo-element
   *
   * @param pseudoElementName pseudo-element name of the element,
   *                          ":before", ":after", ":first-letter", ":first-line", ":selection"
   * @param propertyName      property name of the pseudo-element
   * @return the property value or "" if the property is missing
   * @see com.codeborne.selenide.commands.GetPseudoValue
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String pseudo(String pseudoElementName, String propertyName);

  /**
   * Get content of the pseudo-element
   *
   * @param pseudoElementName pseudo-element name of the element, ":before", ":after"
   * @return the content value or "none" if the content is missing
   * @see com.codeborne.selenide.commands.GetPseudoValue
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String pseudo(String pseudoElementName);

  /**
   * Select radio button
   *
   * @param value value of radio button to select
   * @return selected "input type=radio" element
   * @see com.codeborne.selenide.commands.SelectRadio
   */
  @CanIgnoreReturnValue
  SelenideElement selectRadio(String value);

  /**
   * Get value of attribute "data-<i>dataAttributeName</i>"
   *
   * @see com.codeborne.selenide.commands.GetDataAttribute
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String data(String dataAttributeName);

  /**
   * @see com.codeborne.selenide.commands.GetAttribute
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Override
  @Nullable
  String getAttribute(String name);

  /**
   * @see com.codeborne.selenide.commands.GetCssValue
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Override
  @NonNull
  String getCssValue(String propertyName);

  /**
   * Checks if element exists true on the current page.
   *
   * @return false if element is not found, browser is closed or any WebDriver exception happened
   * @see com.codeborne.selenide.commands.Exists
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  boolean exists();

  /**
   * Check if this element exists and visible.
   *
   * @return false if element does not exist, is invisible, browser is closed or any WebDriver exception happened.
   * @see com.codeborne.selenide.commands.IsDisplayed
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Override
  @CheckReturnValue
  boolean isDisplayed();

  /**
   * immediately returns true if element matches given condition
   * Method doesn't wait!
   *
   * WARNING: This method can help implementing crooks, but it is not needed for typical ui tests.
   *
   * @see #has(WebElementCondition)
   * @see com.codeborne.selenide.commands.Matches
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  boolean is(WebElementCondition condition);

  /**
   * Checks if element matches given condition (with the given timeout)
   * <ol>
   * <li>If matches, immediately returns {@code true}</li>
   * <li>If no, waits (up to given timeout), and if still no, returns {@code false}.</li>
   * </ol>
   * <p>
   *   WARNING: This method can help implementing crooks, but we highly recommend to create a proper solution.
   * </p>
   * <p>
   *   Once again: We DO NOT RECOMMEND using this method.
   * </p>
   *
   * @see com.codeborne.selenide.commands.Matches
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   * @see <a href="https://selenide.org/2019/12/02/advent-calendar-how-to-abuse-selenide/">How to abuse Selenide</a>
   * @since 7.1.0
   */
  boolean is(WebElementCondition condition, Duration timeout);

  /**
   * immediately returns true if element matches given condition
   * Method doesn't wait!
   * WARNING: This method can help implementing crooks, but it is not needed for typical ui tests.
   *
   * @see #is(WebElementCondition)
   * @see com.codeborne.selenide.commands.Matches
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  boolean has(WebElementCondition condition);

  /**
   * Same as {@link #is(WebElementCondition, Duration)}
   * @see com.codeborne.selenide.commands.Matches
   */
  boolean has(WebElementCondition condition, Duration timeout);

  /**
   * Set checkbox state to CHECKED or UNCHECKED.
   *
   * @param selected true for checked and false for unchecked
   * @see com.codeborne.selenide.commands.SetSelected
   */
  @CanIgnoreReturnValue
  SelenideElement setSelected(boolean selected);

  /**
   * Sequentially checks that given element meets all given conditions.<p>
   *
   * IMPORTANT: If element does not match then conditions immediately, waits up to
   * 4 seconds until element meets the conditions. It's extremely useful for dynamic content.<p>
   *
   * Timeout is configurable via {@link com.codeborne.selenide.Configuration#timeout}<p>
   *
   * For example: {@code
   * $("#errorMessage").should(appear);
   * }
   *
   * @return Given element, useful for chaining:
   * {@code $("#errorMessage").should(appear).shouldBe(enabled);}
   * @see com.codeborne.selenide.Config#timeout
   * @see com.codeborne.selenide.commands.Should
   */
  @CanIgnoreReturnValue
  SelenideElement should(WebElementCondition... condition);

  /**
   * Wait until given element meets given condition (with given timeout)
   * @see com.codeborne.selenide.commands.Should
   */
  @CanIgnoreReturnValue
  SelenideElement should(WebElementCondition condition, Duration timeout);

  /**
   * Synonym for {@link #should(WebElementCondition...)}. Useful for better readability.<p>
   *
   * For example: {@code
   * $("#errorMessage").shouldHave(text("Hello"), text("World"));
   * }
   *
   * @see SelenideElement#should(WebElementCondition...)
   * @see com.codeborne.selenide.commands.ShouldHave
   */
  @CanIgnoreReturnValue
  SelenideElement shouldHave(WebElementCondition... condition);

  /**
   * Wait until given element meets given condition (with given timeout)
   * @see com.codeborne.selenide.commands.ShouldHave
   */
  @CanIgnoreReturnValue
  SelenideElement shouldHave(WebElementCondition condition, Duration timeout);

  /**
   * Synonym for {@link #should(WebElementCondition...)}. Useful for better readability.<p>
   *
   * For example: {@code
   * $("#errorMessage").shouldBe(visible, enabled);
   * }
   *
   * @see SelenideElement#should(WebElementCondition...)
   * @see com.codeborne.selenide.commands.ShouldBe
   */
  @CanIgnoreReturnValue
  SelenideElement shouldBe(WebElementCondition... condition);

  /**
   * Wait until given element meets given condition (with given timeout)
   * @see com.codeborne.selenide.commands.ShouldBe
   */
  @CanIgnoreReturnValue
  SelenideElement shouldBe(WebElementCondition condition, Duration timeout);

  /**
   * Sequentially checks that given element does not meet given conditions.<p>
   *
   * IMPORTANT: If element does match the conditions, waits up to
   * 4 seconds until element does not meet the conditions. It's extremely useful for dynamic content.<p>
   *
   * Timeout is configurable via {@link com.codeborne.selenide.Configuration#timeout}<p>
   *
   * For example: {@code
   * $("#errorMessage").should(exist);
   * }
   *
   * @see com.codeborne.selenide.Config#timeout
   * @see com.codeborne.selenide.commands.ShouldNot
   */
  @CanIgnoreReturnValue
  SelenideElement shouldNot(WebElementCondition... condition);

  /**
   * Wait until given element meets given condition (with given timeout)
   * @see com.codeborne.selenide.commands.ShouldNot
   */
  @CanIgnoreReturnValue
  SelenideElement shouldNot(WebElementCondition condition, Duration timeout);

  /**
   * Synonym for {@link #shouldNot(WebElementCondition...)}. Useful for better readability.<p>
   *
   * For example: {@code
   * $("#errorMessage").shouldNotHave(text("Exception"), text("Error"));
   * }
   *
   * @see SelenideElement#shouldNot(WebElementCondition...)
   * @see com.codeborne.selenide.commands.ShouldNotHave
   */
  @CanIgnoreReturnValue
  SelenideElement shouldNotHave(WebElementCondition... condition);

  /**
   * Wait until given element does NOT meet given condition (with given timeout)
   * @see com.codeborne.selenide.commands.ShouldNotHave
   */
  @CanIgnoreReturnValue
  SelenideElement shouldNotHave(WebElementCondition condition, Duration timeout);

  /**
   * Synonym for {@link #shouldNot(WebElementCondition...)}. Useful for better readability.<p>
   *
   * For example: {@code
   * $("#errorMessage").shouldNotBe(visible, enabled);
   * }
   *
   * @see SelenideElement#shouldNot(WebElementCondition...)
   * @see com.codeborne.selenide.commands.ShouldNotBe
   */
  @CanIgnoreReturnValue
  SelenideElement shouldNotBe(WebElementCondition... condition);

  /**
   * Wait until given element does NOT meet given condition (with given timeout)
   * @see com.codeborne.selenide.commands.ShouldNotBe
   */
  @CanIgnoreReturnValue
  SelenideElement shouldNotBe(WebElementCondition condition, Duration timeout);

  /**
   * Short description of WebElement, usually a selector.
   * Useful for logging and debugging.
   * Not recommended to use for test verifications.
   *
   * @return e.g. call to {@code $("#loginButton").toString()} returns {@code "{#loginButton}"}
   * @see com.codeborne.selenide.commands.ToString
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Override
  String toString();

  /**
   * Displays WebElement in human-readable format.
   * Useful for logging and debugging.
   * Not recommended to use for test verifications.
   * May work relatively slowly because it fetches actual element information from browser.
   *
   * @return e.g. <strong id=orderConfirmedStatus class=>Order has been confirmed</strong>
   * @see com.codeborne.selenide.commands.DescribeElement
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  String describe();

  /**
   * @see com.codeborne.selenide.commands.Highlight
   */
  @CanIgnoreReturnValue
  SelenideElement highlight();

  /**
   * @see com.codeborne.selenide.commands.Highlight
   */
  @CanIgnoreReturnValue
  SelenideElement highlight(HighlightOptions options);

  /**
   * Give this element a human-readable name
   *
   * Caution: you probably don't need this method.
   * It's always a good idea to have the actual selector instead of "nice" description (which might be misleading or even lying).
   *
   * @param alias a human-readable name of this element (null or empty string not allowed)
   * @return this element
   * @see com.codeborne.selenide.commands.As
   */
  @CanIgnoreReturnValue
  SelenideElement as(String alias);

  /**
   * Get parent element of this element (lazy evaluation)
   *
   * For example, $("td").parent() could give some "tr".
   *
   * @return Parent element
   * @see com.codeborne.selenide.commands.GetParent
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement parent();

  /**
   * Get the following sibling element of this element
   *
   * For example, $("td").sibling(0) will give the first following sibling element of "td"
   *
   * @param index the index of sibling element
   * @return Sibling element by index
   * @see com.codeborne.selenide.commands.GetSibling
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement sibling(int index);

  /**
   * Get the preceding sibling element of this element
   *
   * For example, $("td").preceding(0) will give the first preceding sibling element of "td"
   *
   * @param index the index of sibling element
   * @return Sibling element by index
   * @see com.codeborne.selenide.commands.GetPreceding
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement preceding(int index);

  /**
   * Get last child element of this element
   *
   * For example, $("tr").lastChild(); could give the last "td".
   *
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   * @see com.codeborne.selenide.commands.GetLastChild
   */
  SelenideElement lastChild();

  /**
   * Locates the closest ancestor element matching given criteria.
   * <br/>
   * For example, $("td").ancestor("table") returns the closest "table" element above "td".
   * <br/>
   * Same as {@code closest("selector", 0)} or {@code closest("selector")}.
   *
   * Examples:
   * <br>
   * {@code $("td").ancestor("table")} will find the closest ancestor with tag {@code table}
   * <br>
   * {@code $("td").ancestor(".container")} will find the closest ancestor with CSS class {@code .container}
   * <br>
   * {@code $("td").ancestor("[data-testid]")} will find the closest ancestor with attribute {@code data-testid}
   * <br>
   * {@code $("td").ancestor("[data-testid=test-value]")} will find the closest ancestor with attribute and
   * attribute's value {@code data-testid=test-value}
   *<br>
   * @param selector Either HTML tag, CSS class, attribute or attribute with value.<br>
   *                 E.g. {@code form}, {@code .active}, {@code [data-testid]}, {@code [data-testid=test-value]}
   * @return Matching ancestor element
   * @see com.codeborne.selenide.commands.Ancestor
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement ancestor(String selector);

  /**
   * Locates the Nth ancestor element matching given criteria.
   * <br/>
   *
   * Examples:
   * <br>
   * {@code $("td").ancestor("table", 1)} will find the 2nd ancestor with tag {@code table}
   * <br>
   * {@code $("td").ancestor(".container", 1)} will find the 2nd ancestor with CSS class {@code .container}
   * <br>
   * {@code $("td").ancestor("[data-testid]", 1)} will find the 2nd ancestor with attribute {@code data-testid}
   * <br>
   * {@code $("td").ancestor("[data-testid=test-value]", 1)} will find the 2nd ancestor with attribute and
   * attribute's value {@code data-testid=test-value}
   *<br>
   * @param selector Either HTML tag, CSS class, attribute or attribute with value.<br>
   *                 E.g. {@code form}, {@code .active}, {@code [data-testid]}, {@code [data-testid=test-value]}
   * @param index    0...N index of the ancestor. 0 is the closest, 1 is higher up the hierarchy, etc...
   * @return Matching ancestor element
   * @see com.codeborne.selenide.commands.Ancestor
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement ancestor(String selector, int index);

  /**
   * Same as {@link #ancestor(String)}.
   *
   * Locates the closest ancestor element matching given criteria.
   * <br/>
   * For example, $("td").closest("table") returns the closest "table" element above "td".
   * <br/>
   * Same as {@code ancestor("selector", 0)}.
   *
   * Examples:
   * <br>
   * {@code $("td").closest("table")} will find the closest ancestor with tag {@code table}
   * <br>
   * {@code $("td").closest(".container")} will find the closest ancestor with CSS class {@code .container}
   * <br>
   * {@code $("td").closest("[data-testid]")} will find the closest ancestor with attribute {@code data-testid}
   * <br>
   * {@code $("td").closest("[data-testid=test-value]")} will find the closest ancestor with attribute and
   * attribute's value {@code data-testid=test-value}
   *<br>
   * @param selector Either HTML tag, CSS class, attribute or attribute with value.<br>
   *                 E.g. {@code form}, {@code .active}, {@code [data-testid]}, {@code [data-testid=test-value]}
   * @return Matching ancestor element
   * @see com.codeborne.selenide.commands.Ancestor
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement closest(String selector);

  /**
   * Locates the first matching element inside given element<p>
   *
   * Short form of {@code webElement.findElement(By.cssSelector(cssSelector))}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement find(String cssSelector);

  /**
   * Locates the Nth matching element inside given element
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement find(String cssSelector, int index);

  /**
   * Same as {@link #find(String)}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement find(By selector);

  /**
   * Same as {@link #find(String, int)}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement find(By selector, int index);

  /**
   * Same as {@link #find(String)}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement $(String cssSelector);

  /**
   * Same as {@link #find(String, int)}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement $(String cssSelector, int index);

  /**
   * Same as {@link #find(String)}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement $(By selector);

  /**
   * Same as {@link #find(String, int)}
   *
   * @see com.codeborne.selenide.commands.Find
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement $(By selector, int index);

  /**
   * Locates the first matching element inside given element using xpath locator<p>
   *
   * Short form of {@code webElement.findElement(By.xpath(xpathLocator))}
   *
   * @see com.codeborne.selenide.commands.FindByXpath
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement $x(String xpath);

  /**
   * Locates the Nth matching element inside given element using xpath locator
   *
   * @see com.codeborne.selenide.commands.FindByXpath
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  SelenideElement $x(String xpath, int index);

  /**
   * Short form of {@code webDriver.findElements(thisElement, By.cssSelector(cssSelector))}
   * <p>
   *
   * For example, {@code $("#multirowTable").findAll("tr.active").shouldHave(size(2));}
   *
   * @return list of elements inside given element matching given CSS selector
   * @see com.codeborne.selenide.commands.FindAll
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  ElementsCollection findAll(String cssSelector);

  /**
   * Short form of {@code webDriver.findElements(thisElement, selector)}
   * <p>
   *
   * For example, {@code $("#multirowTable").findAll(By.className("active")).shouldHave(size(2));}
   *
   * @return list of elements inside given element matching given criteria
   * @see com.codeborne.selenide.commands.FindAll
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  ElementsCollection findAll(By selector);

  /**
   * Same as {@link #findAll(String)}
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  ElementsCollection $$(String cssSelector);

  /**
   * Same as {@link #findAll(By)}
   */
  ElementsCollection $$(By selector);

  /**
   * Short form of {@code webDriver.findElements(thisElement, By.xpath(xpath))}
   * <p>
   *
   * For example, {@code $("#multirowTable").$$x("./input").shouldHave(size(2));}
   *
   * @return list of elements inside given element matching given xpath locator
   * @see com.codeborne.selenide.commands.FindAllByXpath
   * @see <a href="https://github.com/selenide/selenide/wiki/lazy-loading">Lazy loading</a>
   */
  ElementsCollection $$x(String xpath);

  /**
   * Upload file into file upload field. File is searched from classpath.<p>
   * Multiple file upload is also supported. Just pass as many file names as you wish.<p>
   *
   * <b>Applicable for:</b> {@code <input type="file">}.
   *
   * @param fileName name of the file or the relative path in classpath e.g. "files/1.pfd"
   * @return the object of the first file uploaded
   * @throws IllegalArgumentException if any of the files is not found
   * @see com.codeborne.selenide.commands.UploadFileFromClasspath
   */
  @CanIgnoreReturnValue
  File uploadFromClasspath(String... fileName);

  /**
   * Upload file into file upload field.<p>
   * Multiple file upload is also supported. Just pass as many files as you wish.<p>
   *
   * <b>Applicable for:</b> {@code <input type="file">}.
   *
   * @param file file object(s)
   * @return the object of the first file uploaded
   * @throws IllegalArgumentException if any of the files is not found, or other errors
   * @see com.codeborne.selenide.commands.UploadFile
   */
  @CanIgnoreReturnValue
  File uploadFile(File... file);

  /**
   * Select an option from dropdown list (by index)
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @param index from 0 to N (0 means first option)
   * @param otherIndexes other indexes (if you need to select multiple options)
   * @see com.codeborne.selenide.commands.SelectOptionByTextOrIndex
   */
  void selectOption(int index, int... otherIndexes);

  /**
   * Select an option from dropdown list (by text)
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @param text visible text of option
   * @param otherTexts other texts (if you need to select multiple options)
   * @see com.codeborne.selenide.commands.SelectOptionByTextOrIndex
   */
  void selectOption(String text, String... otherTexts);

  /**
   * Select an option from dropdown list that contains given text
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @param text substring of visible text of option
   * @param otherTexts other texts (if you need to select multiple options)
   * @see com.codeborne.selenide.commands.SelectOptionContainingText
   */
  void selectOptionContainingText(String text, String... otherTexts);

  /**
   * Select an option from dropdown list (by value)
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @param value "value" attribute of option
   * @param otherValues other values (if you need to select multiple options)
   * @see com.codeborne.selenide.commands.SelectOptionByValue
   */
  void selectOptionByValue(String value, String... otherValues);

  /**
   * Find (first) selected option from this select field
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @return WebElement for selected &lt;option&gt; element
   * @see com.codeborne.selenide.commands.GetSelectedOption
   */
  SelenideElement getSelectedOption() throws NoSuchElementException;

  /**
   * Find all selected options from this select field
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @return ElementsCollection for selected &lt;option&gt; elements (empty list if no options are selected)
   * @see com.codeborne.selenide.commands.GetSelectedOptions
   */
  ElementsCollection getSelectedOptions();

  /**
   * Find all options from this select field
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @return ElementsCollection for all &lt;option&gt; elements
   * @see com.codeborne.selenide.commands.GetOptions
   */
  ElementsCollection getOptions();

  /**
   * Same as {@link #getOptions()}
   */
  ElementsCollection options();

  /**
   * Get value of selected option in select field
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @see GetSelectedOptionValue
   * @return null if the selected option doesn't have "value" attribute (or the select doesn't have options at all)
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String getSelectedOptionValue();

  /**
   * Get text of selected option in select field
   * <p>
   *
   * <b>Applicable for:</b> <pre>{@code
   * <select>
   *   <option>...<option>
   * </select>
   * }</pre>
   *
   * @return null if there is no selected options (or the select doesn't have options at all)
   * @throws IllegalArgumentException if the element type is not {@code <select/>}
   * @see GetSelectedOptionText
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  @Nullable
  String getSelectedOptionText();

  /**
   * Ask browser to scroll to this element
   *
   * @see com.codeborne.selenide.commands.ScrollTo
   */
  @CanIgnoreReturnValue
  SelenideElement scrollTo();

  /**
   * Ask browser to scroll the element on which it's called into the visible area of the browser window.<p>
   *
   * If <b>alignToTop</b> boolean value is <i>true</i> - the top of the element will be aligned to the top.<p>
   *
   * If <b>alignToTop</b> boolean value is <i>false</i> - the bottom of the element will be aligned to the bottom.<p>
   *
   * <b>Usage:</b>
   * <pre>
   *   element.scrollIntoView(true);
   *   // Corresponds to scrollIntoViewOptions: {block: "start", inline: "nearest"}
   *
   *   element.scrollIntoView(false);
   *   // Corresponds to scrollIntoViewOptions: {block: "end", inline: "nearest"}
   * </pre>
   *
   * @param alignToTop boolean value that indicate how element will be aligned to the visible area of the scrollable ancestor.
   * @see com.codeborne.selenide.commands.ScrollIntoView
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView">Web API reference</a>
   */
  @CanIgnoreReturnValue
  SelenideElement scrollIntoView(boolean alignToTop);

  /**
   * Ask browser to scroll the element on which it's called into the visible area of the browser window.
   * <pre>
   * scrollIntoViewOptions:
   *  * behavior (optional) - Defines the transition animation
   *    1. auto (default)
   *    2. instant
   *    3. smooth
   *  * block (optional)
   *    1. start (default)
   *    2. center
   *    3. end
   *    4. nearest
   *  * inline
   *    1. start
   *    2. center
   *    3. end
   *    4. nearest (default)
   * </pre>
   *
   * <b>Usage:</b>
   * <pre>
   *   element.scrollIntoView("{block: \"end\"}");
   *   element.scrollIntoView("{behavior: \"instant\", block: \"end\", inline: \"nearest\"}");
   * </pre>
   *
   * @param scrollIntoViewOptions is an object with the align properties: behavior, block and inline.
   * @see com.codeborne.selenide.commands.ScrollIntoView
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView">Web API reference</a>
   */
  @CanIgnoreReturnValue
  SelenideElement scrollIntoView(String scrollIntoViewOptions);

  /**
   * Scroll element vertically to the center of viewport.
   * Same as {@code $.scrollIntoView("{block: 'center'}")}
   * @see ScrollIntoCenter
   * @since 7.6.0
   */
  @CanIgnoreReturnValue
  SelenideElement scrollIntoCenter();

  /**
   * Scrolls the element by a specified distance in a specified direction.<p>
   * It takes {@link ScrollOptions} to specify the direction, distance and other options.
   *
   * For example, if you want to scroll the element down by 100 pixels, you can do:
   *
   * <pre>
   * {@code element.scroll(ScrollOptions.direction(ScrollDirection.DOWN).distance(100))}
   * </pre>
   *
   * If you want to scroll the element right by 250 pixels, you can do:
   *
   * <pre>
   * {@code element.scroll(ScrollOptions.direction(ScrollDirection.RIGHT).distance(250))}
   * </pre>
   *
   * @param scrollOptions direction, distance etc.
   * @see com.codeborne.selenide.commands.Scroll
   */
  @CanIgnoreReturnValue
  SelenideElement scroll(ScrollOptions scrollOptions);

  /**
   * Download file by clicking this element. Algorithm depends on {@code @{@link Config#fileDownload() }}.
   *
   * @throws RuntimeException      if 50x status code was returned from server
   * @throws FileNotDownloadedError     if 40x status code was returned from server
   * @see FileDownloadMode
   * @see com.codeborne.selenide.commands.DownloadFile
   */
  File download() throws FileNotDownloadedError;

  /**
   * Download file by clicking this element. Algorithm depends on {@code @{@link Config#fileDownload() }}.
   *
   * @param timeout download operations timeout.
   * @throws RuntimeException      if 50x status code was returned from server
   * @throws FileNotDownloadedError     if 40x status code was returned from server
   * @see com.codeborne.selenide.commands.DownloadFile
   * @deprecated Use method {{@link #download(DownloadOptions)}} instead
   */
  @Deprecated
  File download(long timeout) throws FileNotDownloadedError;

  /**
   * Download file by clicking this element. Algorithm depends on {@code @{@link Config#fileDownload() }}.
   *
   * @param fileFilter Criteria for defining which file is expected (
   *                   {@link com.codeborne.selenide.files.FileFilters#withName(String)},
   *                   {@link com.codeborne.selenide.files.FileFilters#withNameMatching(String)},
   *                   {@link com.codeborne.selenide.files.FileFilters#withName(String)}
   *                   ).
   * @throws RuntimeException   if 50x status code was returned from server
   * @throws FileNotDownloadedError  if 40x status code was returned from server, or the downloaded file didn't match given filter.
   * @see com.codeborne.selenide.files.FileFilters
   * @see com.codeborne.selenide.commands.DownloadFile
   */
  File download(FileFilter fileFilter) throws FileNotDownloadedError;

  /**
   * Download file by clicking this element. Algorithm depends on {@code @{@link Config#fileDownload() }}.
   *
   * @param timeout    download operations timeout.
   * @param fileFilter Criteria for defining which file is expected (
   *                   {@link com.codeborne.selenide.files.FileFilters#withName(String)},
   *                   {@link com.codeborne.selenide.files.FileFilters#withNameMatching(String)},
   *                   {@link com.codeborne.selenide.files.FileFilters#withName(String)}
   *                   ).
   * @throws RuntimeException      if 50x status code was returned from server
   * @throws FileNotDownloadedError     if 40x status code was returned from server, or the downloaded file didn't match given filter.
   * @see com.codeborne.selenide.files.FileFilters
   * @see com.codeborne.selenide.commands.DownloadFile
   * @deprecated Use method {{@link #download(DownloadOptions)}} instead
   */
  @Deprecated
  File download(long timeout, FileFilter fileFilter) throws FileNotDownloadedError;

  /**
   * @see com.codeborne.selenide.commands.DownloadFile
   */
  File download(DownloadOptions options) throws FileNotDownloadedError;

  /**
   * Return criteria by which this element is located
   *
   * @return e.g. "#multirowTable.findBy(text 'INVALID-TEXT')/valid-selector"
   * @see com.codeborne.selenide.commands.GetSearchCriteria
   */
  String getSearchCriteria();

  /**
   * @return the original Selenium {@link WebElement} wrapped by this object
   * @throws org.openqa.selenium.NoSuchElementException if element does not exist (without waiting for the element)
   * @see com.codeborne.selenide.commands.ToWebElement
   */
  WebElement toWebElement();

  /**
   * @return Underlying {@link WebElement}
   * @throws com.codeborne.selenide.ex.ElementNotFound if element does not exist (after waiting for N seconds)
   * @see com.codeborne.selenide.commands.GetWrappedElement
   */
  @Override
  @NonNull
  WebElement getWrappedElement();

  /**
   * Cache this web element. The following calls to this object will not re-load this element from browser.
   * @see com.codeborne.selenide.commands.CacheSelenideElement
   */
  SelenideElement cached();

  /**
   * Click the element using {@link ClickOptions}:
   *
   * <pre>
   *  {@code $("#username").click(ClickOptions.usingJavaScript())}
   * </pre>
   *
   * You can specify a relative offset from the center of the element inside ClickOptions:
   * e.g. <pre>
   *  {@code $("#username").click(usingJavaScript().offset(123, 222))}
   * </pre>
   *
   * Before clicking, waits until element gets interactable and enabled.
   * <br>
   *
   * @return this element
   * @see com.codeborne.selenide.commands.Click
   */
  @CanIgnoreReturnValue
  SelenideElement click(ClickOptions clickOption);

  /**
   * Click the element<p>
   *
   * By default, it uses default Selenium method click.<p>
   *
   * But it uses JavaScript method to click if {@code com.codeborne.selenide.Configuration#clickViaJs} is defined.
   * It may be helpful for testing in Internet Explorer where native click doesn't always work correctly.
   *
   * <br><br>
   * Before clicking, waits until element gets interactable and enabled.
   * <br>
   *
   * @see com.codeborne.selenide.commands.Click
   */
  @CanIgnoreReturnValue
  @Override
  void click();

  /**
   * Click with right mouse button on this element
   *
   * @return this element
   * @see com.codeborne.selenide.commands.ContextClick
   */
  @CanIgnoreReturnValue
  SelenideElement contextClick();

  /**
   * Double-click the element
   *
   * <br><br>
   * Before clicking, waits until element gets interactable and enabled.
   * <br>
   *
   * @return this element
   * @see com.codeborne.selenide.commands.DoubleClick
   */
  @CanIgnoreReturnValue
  SelenideElement doubleClick();

  /**
   * Double-click the element using {@link ClickOptions}: {@code $("#username").doubleClick(ClickOptions.usingJavaScript())}
   *
   * <p>
   * You can specify a relative offset from the center of the element inside ClickOptions:
   * e.g. {@code $("#username").doubleClick(usingJavaScript().offset(123, 222))}
   * </p>
   *
   * <br>
   * Before clicking, waits until element gets interactable and enabled.
   * <br>
   *
   * @return this element
   * @see com.codeborne.selenide.commands.DoubleClick
   */
  @CanIgnoreReturnValue
  SelenideElement doubleClick(ClickOptions clickOption);

  /**
   * Emulate "mouseOver" event. In other words, move mouse cursor over this element (without clicking it).
   *
   * @return this element
   * @see com.codeborne.selenide.commands.Hover
   */
  @CanIgnoreReturnValue
  SelenideElement hover();

  /**
   * Emulate "mouseOver" event. In other words, move mouse cursor over this element (without clicking it).
   *
   * @param options optional hover parameters (offset etc)
   * @return this element
   * @see com.codeborne.selenide.commands.Hover
   */
  @CanIgnoreReturnValue
  SelenideElement hover(HoverOptions options);

  /**
   * Drag and drop this element to the target
   * <br>
   * Before dropping, waits until target element gets visible.
   * <br>
   *
   * Examples:
   * <br>
   * using a CSS selector defining the target element:
   * <br>
   * {@code $("#element").dragAndDrop(to("#target")) }
   * <br>
   * using a SelenideElement defining the target element:
   * <br>
   * {@code $("#element").dragAndDrop(to($("#target"))) }
   * <br>
   * <br>
   * define which way it will be executed:
   * <br>
   * using {@code JavaScript } (by default):
   * <br>
   * {@code $("#element").dragAndDrop(to("#target").usingJS()) }
   * <br>
   * using {@code Actions }:
   * <br>
   * {@code $("#element").dragAndDrop(to("#target").usingSeleniumActions()) }
   * <br>
  / *
   * @param options drag and drop options to define target and which way it will be executed
   *
   * @return this element
   * @see com.codeborne.selenide.commands.DragAndDrop
   */
  @CanIgnoreReturnValue
  SelenideElement dragAndDrop(DragAndDropOptions options);

  /**
   * Execute custom implemented command (this command will not receive
   * any arguments through {@link Command#execute(SelenideElement, WebElementSource, Object[])}
   * when executed).
   *
   * @param command custom command
   * @return whatever the command returns (incl. null)
   * @see com.codeborne.selenide.commands.Execute
   * @see com.codeborne.selenide.Command
   */
  @Nullable
  @CanIgnoreReturnValue
  <ReturnType> ReturnType execute(Command<ReturnType> command);

  @CanIgnoreReturnValue
  <ReturnType extends SelenideElement> ReturnType execute(FluentCommand command);

  /**
   * Execute custom implemented command with given timeout (this command will not receive
   * any arguments through {@link Command#execute(SelenideElement, WebElementSource, Object[])}
   * when executed).
   *
   * @param command custom command
   * @param timeout given timeout
   * @return whatever the command returns (incl. null)
   * @see com.codeborne.selenide.commands.Execute
   * @see com.codeborne.selenide.Command
   */
  @Nullable
  @CanIgnoreReturnValue
  <ReturnType> ReturnType execute(Command<ReturnType> command, Duration timeout);

  /**
   * Check if image is properly loaded.
   * <p>
   * <b>Applicable for:</b> {@code <img src="...">}.
   *
   * @throws IllegalArgumentException if argument is not an "img" element
   * @see com.codeborne.selenide.commands.IsImage
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  boolean isImage();

  /**
   * Take screenshot of this element
   *
   * @return file with screenshot (*.png)
   * or null if Selenide failed to take a screenshot (due to some technical problem)
   * @see com.codeborne.selenide.commands.TakeScreenshot
   */
  @Nullable
  File screenshot();

  /**
   * Take screenshot of this element
   *
   * @return buffered image with screenshot
   * @see com.codeborne.selenide.commands.TakeScreenshotAsImage
   */
  @Nullable
  BufferedImage screenshotAsImage();
}
