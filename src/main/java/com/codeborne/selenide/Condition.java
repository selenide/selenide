package com.codeborne.selenide;

import com.codeborne.selenide.conditions.And;
import com.codeborne.selenide.conditions.Attribute;
import com.codeborne.selenide.conditions.AttributeWithValue;
import com.codeborne.selenide.conditions.Animated;
import com.codeborne.selenide.conditions.CaseSensitiveText;
import com.codeborne.selenide.conditions.Checked;
import com.codeborne.selenide.conditions.CssClass;
import com.codeborne.selenide.conditions.CssValue;
import com.codeborne.selenide.conditions.CustomMatch;
import com.codeborne.selenide.conditions.Disabled;
import com.codeborne.selenide.conditions.Editable;
import com.codeborne.selenide.conditions.Enabled;
import com.codeborne.selenide.conditions.ExactOwnText;
import com.codeborne.selenide.conditions.ExactOwnTextCaseSensitive;
import com.codeborne.selenide.conditions.ExactText;
import com.codeborne.selenide.conditions.ExactTextCaseSensitive;
import com.codeborne.selenide.conditions.Exist;
import com.codeborne.selenide.conditions.Focused;
import com.codeborne.selenide.conditions.Hidden;
import com.codeborne.selenide.conditions.Href;
import com.codeborne.selenide.conditions.InnerText;
import com.codeborne.selenide.conditions.Interactable;
import com.codeborne.selenide.conditions.IsImageLoaded;
import com.codeborne.selenide.conditions.MatchAttributeWithValue;
import com.codeborne.selenide.conditions.MatchText;
import com.codeborne.selenide.conditions.NamedCondition;
import com.codeborne.selenide.conditions.OneOfExactTexts;
import com.codeborne.selenide.conditions.OneOfExactTextsCaseSensitive;
import com.codeborne.selenide.conditions.OneOfTexts;
import com.codeborne.selenide.conditions.OneOfTextsCaseSensitive;
import com.codeborne.selenide.conditions.Or;
import com.codeborne.selenide.conditions.OwnText;
import com.codeborne.selenide.conditions.OwnTextCaseSensitive;
import com.codeborne.selenide.conditions.PartialText;
import com.codeborne.selenide.conditions.PartialTextCaseSensitive;
import com.codeborne.selenide.conditions.PartialValue;
import com.codeborne.selenide.conditions.PseudoElementPropertyWithValue;
import com.codeborne.selenide.conditions.Readonly;
import com.codeborne.selenide.conditions.Selected;
import com.codeborne.selenide.conditions.SelectedText;
import com.codeborne.selenide.conditions.TagName;
import com.codeborne.selenide.conditions.Text;
import com.codeborne.selenide.conditions.Value;
import com.codeborne.selenide.conditions.Visible;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

import static com.codeborne.selenide.conditions.ConditionHelpers.merge;
import static java.util.Arrays.asList;

/**
 * Conditions to match web elements: checks for visibility, text etc.
 */
@ParametersAreNonnullByDefault
public final class Condition {
  /**
   * Checks if element is visible
   *
   * <p>Sample: {@code $("input").shouldBe(visible);}</p>
   */
  public static final WebElementCondition visible = new Visible();

  /**
   * Check if element exist. It can be visible or hidden.
   *
   * <p>Sample: {@code $("input").should(exist);}</p>
   */
  public static final WebElementCondition exist = new Exist();

  /**
   * Checks that element is not visible or does not exist.
   * <p>
   * Opposite to {@link #appear}
   *
   * <p>Sample: {@code $("input").shouldBe(hidden);}</p>
   */
  public static final WebElementCondition hidden = new Hidden();

  /**
   * Synonym for {@link #visible} - may be used for better readability
   *
   * <p>Sample: {@code $("#logoutLink").should(appear);}</p>
   */
  public static final WebElementCondition appear = be(visible);

  /**
   * Synonym for {@link #hidden} - may be used for better readability:
   *
   * <p>{@code $("#loginLink").should(disappear);}</p>
   */
  public static final WebElementCondition disappear = be(hidden);

  /**
   * Check if element is interactable:
   * <ol>
   *  <li>either is visible, or</li>
   *  <li>has css property "opacity: 0"</li>
   * </ol>
   * <p>
   *   Elements which are transparent (opacity:0) are considered to be invisible, but interactable.
   *   User can click, doubleClick etc., and enter text etc. to transparent elements
   *   (for all major browsers).
   * </p>
   * <br/>
   * <p>Example:</p>
   * <p>{@code $("input[type=file]").shouldBe(interactable);}</p>
   * <br/>
   * @since 6.5.0
   */
  public static final WebElementCondition interactable = new Interactable();

  /**
   * <p>
   *   Check if element has "readonly" attribute (with any value)
   * </p>
   * <br>
   * <p>Sample:</p>
   * <p>{@code $("input").shouldBe(readonly);}</p>
   * <br>
   */
  public static final WebElementCondition readonly = new Readonly();

  /**
   * Check if element is "editable":
   * <ul>
   * <li>is {@link #interactable}, and</li>
   * <li>is {@link #enabled}, and</li>
   * <li>is not {@link #readonly}</li>
   * </ul>
   * <br>
   * <p>Sample: {@code $("input").shouldBe(editable);}</p>
   * <br>
   * @since 6.5.0
   */
  public static final WebElementCondition editable = new Editable();

  /**
   * <p>
   *  Check that the element is animated. An animated element changes its position or size over time.
   *  Implemented for web browser context only.
   * </p>
   * <br>
   * <p>Sample:</p>
   * <p>{@code $("popup").shouldBe(animated);}</p>
   * <br>
   * @since v7.0.7
   */
  public static final WebElementCondition animated = new Animated();

  /**
   * Check if element has given attribute (with any value)
   *
   * <p>Sample: {@code $("#mydiv").shouldHave(attribute("fileId"));}</p>
   *
   * @param attributeName name of attribute, not null
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition attribute(String attributeName) {
    return new Attribute(attributeName);
  }

  /**
   * <p>Sample: {@code $("#mydiv").shouldHave(attribute("fileId", "12345"));}</p>
   *
   * @param attributeName          name of attribute
   * @param expectedAttributeValue expected value of attribute
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition attribute(String attributeName, String expectedAttributeValue) {
    return new AttributeWithValue(attributeName, expectedAttributeValue);
  }

  /**
   * Assert that given element's attribute matches given regular expression
   *
   * <p>Sample: {@code $("h1").shouldHave(attributeMatching("fileId", ".*12345.*"))}</p>
   *
   * @param attributeName  name of attribute
   * @param attributeRegex regex to match attribute value
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition attributeMatching(String attributeName, String attributeRegex) {
    return new MatchAttributeWithValue(attributeName, attributeRegex);
  }

  /**
   * <p>Sample: {@code $("#mydiv").shouldHave(href("/one/two/three.pdf"));}</p>
   * <p>
   * It looks similar to `$.shouldHave(attribute("href", href))`, but
   * it overcomes the fact that Selenium returns full url (even if "href" attribute in html contains relative url).
   *
   * @param href expected value of "href" attribute
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition href(String href) {
    return new Href(href);
  }

  /**
   * Assert that element contains given "value" attribute as substring
   * NB! Ignores difference in non-visible characters like spaces, non-breakable spaces, tabs, newlines  etc.
   *
   * <p>Sample: {@code $("input").shouldHave(value("12345 666 77"));}</p>
   *
   * @param expectedValue expected value of "value" attribute
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition value(String expectedValue) {
    return new Value(expectedValue);
  }

  /**
   * Assert that element contains given "value" attribute as substring
   * NB! Ignores difference in non-visible characters like spaces, non-breakable spaces, tabs, newlines  etc.
   *
   * <p>Sample: {@code $("input").shouldHave(partialValue("12345 666 77"));}</p>
   *
   * @param expectedValue expected value of "value" attribute
   * @since 6.7.3
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition partialValue(String expectedValue) {
    return new PartialValue(expectedValue);
  }

  /**
   * Check that element has given the property value of the pseudo-element
   * <p>Sample: {@code $("input").shouldHave(pseudo(":first-letter", "color", "#ff0000"));}</p>
   *
   * @param pseudoElementName pseudo-element name of the element,
   *                          ":before", ":after", ":first-letter", ":first-line", ":selection"
   * @param propertyName      property name of the pseudo-element
   * @param expectedValue     expected value of the property
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition pseudo(String pseudoElementName, String propertyName, String expectedValue) {
    return new PseudoElementPropertyWithValue(pseudoElementName, propertyName, expectedValue);
  }

  /**
   * Check that element has given the "content" property of the pseudo-element
   * <p>Sample: {@code $("input").shouldHave(pseudo(":before", "Hello"));}</p>
   *
   * @param pseudoElementName pseudo-element name of the element, ":before", ":after"
   * @param expectedValue     expected content of the pseudo-element
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition pseudo(String pseudoElementName, String expectedValue) {
    return new PseudoElementPropertyWithValue(pseudoElementName, "content", expectedValue);
  }

  /**
   * <p>Sample: {@code $("#input").shouldHave(exactValue("John"));}</p>
   *
   * @param value expected value of input field
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition exactValue(String value) {
    return attribute("value", value);
  }

  /**
   * Asserts the name attribute of the element to be exact string
   * <p>Sample: {@code $("#input").shouldHave(name("username"))}</p>
   *
   * @param name expected name of input field
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition name(String name) {
    return attribute("name", name);
  }

  /**
   * Asserts the type attribute of the element to be exact string
   * <p>Sample: {@code $("#input").shouldHave(type("checkbox"))}</p>
   *
   * @param type expected type of input field
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition type(String type) {
    return attribute("type", type);
  }

  /**
   * <p>Sample: {@code $("#input").shouldHave(id("myForm"))}</p>
   *
   * @param id expected id of input field
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition id(String id) {
    return attribute("id", id);
  }

  /**
   * 1) For input element, check that value is missing or empty
   * <p>Sample: {@code $("#input").shouldBe(empty)}</p>
   * <p>
   * 2) For other elements, check that text is empty
   * <p>Sample: {@code $("h2").shouldBe(empty)}</p>
   */
  public static final WebElementCondition empty = and("empty", exactValue(""), exactText(""));

  /**
   * Assert that given element's text matches given regular expression
   *
   * <p>Sample: {@code $("h1").should(matchText("Hello\s*John"))}</p>
   *
   * @param regex e.g. Kicked.*Chuck Norris - in this case ".*" can contain any characters including spaces, tabs, CR etc.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition matchText(String regex) {
    return new MatchText(regex);
  }

  /**
   * Assert that given element's TEXT case-insensitively CONTAINS at least
   * one of the given {@code texts}. Assertion fails if specified collection is empty.
   *
   * <p>NB! Ignores multiple whitespaces between words.</p>
   * <p>NB! Nulls and blank strings are not allowed in the specified collection
   * (because any element does contain an empty text).</p>
   * @throws IllegalArgumentException If specified collection contains {@code null}s or blank strings.
   * @since 7.0.3
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition oneOfTexts(String... texts) {
    return new OneOfTexts(asList(texts));
  }

  /**
   * Assert that given element's TEXT case-sensitively CONTAINS at least
   * one of the given {@code texts}. Assertion fails if specified collection is empty.
   *
   * <p>NB! Ignores multiple whitespaces between words.</p>
   * <p>NB! Nulls and blank strings are not allowed in the specified collection
   * (because any element does contain an empty text).</p>
   * @throws IllegalArgumentException If specified collection contains {@code null}s or blank strings.
   * @since 7.0.3
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition oneOfTextsCaseSensitive(String... texts) {
    return new OneOfTextsCaseSensitive(asList(texts));
  }

  /**
   * Assert that given element's TEXT case-insensitively EQUALS to
   * one of the given {@code texts}. Assertion fails if specified collection is empty.
   *
   * <p>NB! Ignores multiple whitespaces between words.</p>
   * @throws IllegalArgumentException If specified collection contains {@code null} elements.
   * @since 7.0.3
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition oneOfExactTexts(String... texts) {
    return new OneOfExactTexts(asList(texts));
  }

  /**
   * Assert that given element's TEXT case-sensitively EQUALS to
   * one of the given {@code texts}. Assertion fails if specified collection is empty.
   *
   * <p>NB! Ignores multiple whitespaces between words.</p>
   * @throws IllegalArgumentException If specified collection contains {@code null} elements.
   * @since 7.0.3
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition oneOfExactTextsCaseSensitive(String... texts) {
    return new OneOfExactTextsCaseSensitive(asList(texts));
  }

  /**
   * Assert that given element's text CONTAINS given text
   *
   * <p>Sample: {@code $("h1").shouldHave(partialText("ello Joh"))}</p>
   *
   * @since 6.7.0
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition partialText(String expectedText) {
    return new PartialText(expectedText);
  }

  /**
   * Assert that given element's text CONTAINS given text (case-sensitive)
   *
   * <p>Sample: {@code $("h1").should(partialTextCaseSensitive("ELLO jOH"))}</p>
   *
   * @since 6.7.0
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition partialTextCaseSensitive(String expectedText) {
    return new PartialTextCaseSensitive(expectedText);
  }

  /**
   * <p>
   * Assert that element contains given text as a substring.
   * </p>
   *
   * <p>Sample: {@code $("h1").shouldHave(text("Hello\s*John"))}</p>
   *
   * <p>NB! Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element.
   *             NB! Empty string is not allowed (because any element does contain an empty text).
   * @throws IllegalArgumentException if given text is null or empty
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition text(String text) {
    return new Text(text);
  }

  /**
   * Checks on {@code <a>} element that exactly given text is selected (=marked with mouse/keyboard)
   *
   * <p>Sample: {@code $("input").shouldHave(selectedText("Text"))}</p>
   *
   * <p>NB! Case sensitive</p>
   *
   * @param expectedText expected selected text of the element
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition selectedText(String expectedText) {
    return new SelectedText(expectedText);
  }

  /**
   * Assert that element contains given text as a case-sensitive substring
   *
   * <p>Sample: {@code $("h1").shouldHave(textCaseSensitive("Hello\s*John"))}</p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition textCaseSensitive(String text) {
    return new CaseSensitiveText(text);
  }

  /**
   * Assert that element has exactly (case-insensitive) given text
   * <p>Sample: {@code $("h1").shouldHave(exactText("Hello"))}</p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition exactText(String text) {
    return new ExactText(text);
  }

  /**
   * Assert that element contains given inner text.
   * <p>Sample: {@code $("h1").shouldHave(innerText("Hello"))}</p>
   *
   * It can be used to check the text of a hidden element.
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition innerText(String text) {
    return new InnerText(text);
  }

  /**
   * Assert that element contains given text (without checking child elements).
   * <p>Sample: {@code $("h1").shouldHave(ownText("Hello"))}</p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element without its children
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition ownText(String text) {
    return new OwnText(text);
  }

  /**
   * Assert that element contains given text (without checking child elements).
   * <p>Sample: {@code $("h1").shouldHave(ownTextCaseSensitive("Hello"))}</p>
   *
   * <p>Case sensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element without its children
   * @since 6.6.0
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition ownTextCaseSensitive(String text) {
    return new OwnTextCaseSensitive(text);
  }

  /**
   * Assert that element has given text (without checking child elements).
   * <p>Sample: {@code $("h1").shouldHave(ownText("Hello"))}</p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element without its children
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition exactOwnText(String text) {
    return new ExactOwnText(text);
  }

  /**
   * Assert that element has given text (without checking child elements).
   * <p>Sample: {@code $("h1").shouldHave(exactOwnTextCaseSensitive("Hello"))}</p>
   *
   * <p>Case sensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element without its children
   * @since 6.6.0
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition exactOwnTextCaseSensitive(String text) {
    return new ExactOwnTextCaseSensitive(text);
  }

  /**
   * Assert that element has exactly the given text
   * <p>Sample: {@code $("h1").shouldHave(exactTextCaseSensitive("Hello"))}</p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition exactTextCaseSensitive(String text) {
    return new ExactTextCaseSensitive(text);
  }

  /**
   * Asserts that element has the given tag name.
   * <p>Sample: {@code $(".btn-primary").shouldHave(tagName("button"));}</p>
   * @since 6.7.3
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition tagName(String cssClass) {
    return new TagName(cssClass);
  }

  /**
   * Asserts that element has the given class. Element may have other classes as well.
   * <p>Sample: {@code $("input").shouldHave(cssClass("active"));}</p>
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition cssClass(String cssClass) {
    return new CssClass(cssClass);
  }

  /**
   * Checks if css property (style) applies for the element.
   * Both explicit and computed properties are supported.
   * <p>
   * Note that if css property is missing {@link WebElement#getCssValue} return empty string.
   * In this case you should assert against empty string.
   * <p>
   * Sample:
   * <p>
   * {@code <input style="font-size: 12">}
   * <p>
   * {@code $("input").shouldHave(cssValue("font-size", "12"));}
   * <p>
   * {@code $("input").shouldHave(cssValue("display", "block"));}
   *
   * @param propertyName  the css property (style) name  of the element
   * @param expectedValue expected value of css property
   * @see WebElement#getCssValue
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition cssValue(String propertyName, @Nullable String expectedValue) {
    return new CssValue(propertyName, expectedValue);
  }

  /**
   * Checks if element matches the given predicate.
   *
   * <p>Sample: {@code $("input").should(match("empty value attribute", el -> el.getAttribute("value").isEmpty()));}</p>
   *
   * @param description the description of the predicate
   * @param predicate   the {@link Predicate} to match
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition match(String description, Predicate<WebElement> predicate) {
    return new CustomMatch(description, predicate);
  }

  /**
   * Check if image is loaded.
   */
  public static final WebElementCondition image = new IsImageLoaded();

  /**
   * Check if browser focus is currently in given element.
   */
  public static final WebElementCondition focused = new Focused();

  /**
   * Checks that element is not disabled
   *
   * @see WebElement#isEnabled()
   */
  public static final WebElementCondition enabled = new Enabled();

  /**
   * Checks that element is disabled
   *
   * @see WebElement#isEnabled()
   */
  public static final WebElementCondition disabled = new Disabled();

  /**
   * Checks that element is selected (inputs like drop-downs etc.)
   *
   * @see WebElement#isSelected()
   */
  public static final WebElementCondition selected = new Selected();

  /**
   * Checks that checkbox is checked
   *
   * @see WebElement#isSelected()
   */
  public static final WebElementCondition checked = new Checked();

  /**
   * Negate given condition.
   * <p>
   * Used for methods like $.shouldNot(exist), $.shouldNotBe(visible)
   * <p>
   * Typically, you don't need to use it.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition not(WebElementCondition condition) {
    return condition.negate();
  }

  /**
   * Check if element matches ALL given conditions.
   * The method signature makes you to pass at least 2 conditions, otherwise it would be nonsense.
   *
   * @param name       Name of this condition, like "empty" (meaning e.g. empty text AND empty value).
   * @param condition1 first condition to match
   * @param condition2 second condition to match
   * @param conditions Other conditions to match
   * @return logical AND for given conditions.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition and(String name, WebElementCondition condition1, WebElementCondition condition2,
                                        WebElementCondition... conditions) {
    return new And(name, merge(condition1, condition2, conditions));
  }

  /**
   * Synonym for {@link #and(String, WebElementCondition, WebElementCondition, WebElementCondition...)}.
   * Useful for better readability.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition allOf(String name, WebElementCondition condition1, WebElementCondition condition2,
                                          WebElementCondition... conditions) {
    return and(name, condition1, condition2, conditions);
  }

  /**
   * Synonym for {@link #and(String, WebElementCondition, WebElementCondition, WebElementCondition...)}
   * with "all of" name. Useful for better readability.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition allOf(WebElementCondition condition1, WebElementCondition condition2,
                                          WebElementCondition... conditions) {
    return and("all of", condition1, condition2, conditions);
  }

  /**
   * Check if element matches ANY of given conditions.
   * The method signature makes you to pass at least 2 conditions, otherwise it would be nonsense.
   *
   * Using "or" checks in tests is probably a flag of bad test design.
   * Consider splitting this "or" check into two different methods or tests.
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   *
   * @param name       Name of this condition, like "error" (meaning e.g. "error" OR "failed").
   * @param condition1 first condition to match
   * @param condition2 second condition to match
   * @param conditions Other conditions to match
   * @return logical OR for given conditions.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition or(String name, WebElementCondition condition1, WebElementCondition condition2,
                                       WebElementCondition... conditions) {
    return new Or(name, merge(condition1, condition2, conditions));
  }

  /**
   * Synonym for {@link #or(String, WebElementCondition, WebElementCondition, WebElementCondition...)}.
   * Useful for better readability.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition anyOf(String name, WebElementCondition condition1, WebElementCondition condition2,
                                          WebElementCondition... conditions) {
    return or(name, condition1, condition2, conditions);
  }

  /**
   * Synonym for {@link #or(String, WebElementCondition, WebElementCondition, WebElementCondition...)}
   * with "any of" name. Useful for better readability.
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition anyOf(WebElementCondition condition1, WebElementCondition condition2,
                                          WebElementCondition... conditions) {
    return or("any of", condition1, condition2, conditions);
  }

  /**
   * Check if element is clickable: {@link #interactable} AND {@link #enabled}.
   *
   * Usually you don't need to use this condition.
   * When you just call {@code $("button").click()}, Selenide automatically checks that the element is clickable.
   *
   * <br/>
   * <p>Example:</p>
   * <p>{@code $("input[type=button]").shouldBe(clickable);}</p>
   * <br/>
   * @since 7.2.0
   */
  public static final WebElementCondition clickable = and("clickable", interactable, enabled);

  /**
   * Used to form human-readable condition expression
   * Example element.should(be(visible),have(text("abc"))
   *
   * @param delegate next condition to wrap
   * @return WebElementCondition
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition be(WebElementCondition delegate) {
    return wrap("be", delegate);
  }

  /**
   * Used to form human-readable condition expression
   * Example element.should(be(visible),have(text("abc"))
   *
   * @param delegate next condition to wrap
   * @return WebElementCondition
   */
  @CheckReturnValue
  @Nonnull
  public static WebElementCondition have(WebElementCondition delegate) {
    return wrap("have", delegate);
  }

  private static WebElementCondition wrap(String prefix, WebElementCondition delegate) {
    return new NamedCondition(prefix, delegate);
  }
}
