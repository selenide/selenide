package com.codeborne.selenide;

import com.codeborne.selenide.conditions.And;
import com.codeborne.selenide.conditions.Attribute;
import com.codeborne.selenide.conditions.AttributeWithValue;
import com.codeborne.selenide.conditions.CaseSensitiveText;
import com.codeborne.selenide.conditions.Checked;
import com.codeborne.selenide.conditions.CssClass;
import com.codeborne.selenide.conditions.CssValue;
import com.codeborne.selenide.conditions.CustomMatch;
import com.codeborne.selenide.conditions.Disabled;
import com.codeborne.selenide.conditions.Enabled;
import com.codeborne.selenide.conditions.ExactOwnText;
import com.codeborne.selenide.conditions.ExactText;
import com.codeborne.selenide.conditions.ExactTextCaseSensitive;
import com.codeborne.selenide.conditions.Exist;
import com.codeborne.selenide.conditions.ExplainedCondition;
import com.codeborne.selenide.conditions.Focused;
import com.codeborne.selenide.conditions.Hidden;
import com.codeborne.selenide.conditions.Href;
import com.codeborne.selenide.conditions.IsImageLoaded;
import com.codeborne.selenide.conditions.MatchAttributeWithValue;
import com.codeborne.selenide.conditions.MatchText;
import com.codeborne.selenide.conditions.NamedCondition;
import com.codeborne.selenide.conditions.Not;
import com.codeborne.selenide.conditions.Or;
import com.codeborne.selenide.conditions.OwnText;
import com.codeborne.selenide.conditions.PseudoElementPropertyWithValue;
import com.codeborne.selenide.conditions.Selected;
import com.codeborne.selenide.conditions.SelectedText;
import com.codeborne.selenide.conditions.Text;
import com.codeborne.selenide.conditions.Value;
import com.codeborne.selenide.conditions.Visible;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

/**
 * Conditions to match web elements: checks for visibility, text etc.
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
@ParametersAreNonnullByDefault
public abstract class Condition {
  /**
   * Checks if element is visible
   *
   * <p>Sample: {@code $("input").shouldBe(visible);}</p>
   */
  public static final Condition visible = new Visible();

  /**
   * Check if element exist. It can be visible or hidden.
   *
   * <p>Sample: {@code $("input").should(exist);}</p>
   */
  public static final Condition exist = new Exist();

  /**
   * Checks that element is not visible or does not exists.
   * <p>
   * Opposite to {@link #appear}
   *
   * <p>Sample: {@code $("input").shouldBe(hidden);}</p>
   */
  public static final Condition hidden = new Hidden();

  /**
   * Synonym for {@link #visible} - may be used for better readability
   *
   * <p>Sample: {@code $("#logoutLink").should(appear);}</p>
   */
  public static final Condition appear = visible;

  /**
   * Synonym for {@link #visible} - may be used for better readability
   * <p><code>$("#logoutLink").waitUntil(appears, 10000);</code></p>
   * <p>
   */
  public static final Condition appears = visible;

  /**
   * Synonym for {@link #hidden} - may be used for better readability:
   *
   * <p>Sample: <code>$("#loginLink").waitUntil(disappears, 9000);</code></p>
   */
  public static final Condition disappears = hidden;

  /**
   * Synonym for {@link #hidden} - may be used for better readability:
   *
   * <p><code>$("#loginLink").should(disappear);</code></p>
   */
  public static final Condition disappear = hidden;

  /**
   * Check if element has "readonly" attribute (with any value)
   *
   * <p>Sample: <code>$("input").shouldBe(readonly);</code></p>
   */
  public static final Condition readonly = attribute("readonly");

  /**
   * Check if element has given attribute (with any value)
   *
   * <p>Sample: <code>$("#mydiv").shouldHave(attribute("fileId"));</code></p>
   *
   * @param attributeName name of attribute, not null
   * @return true iff attribute exists
   */
  @CheckReturnValue
  @Nonnull
  public static Condition attribute(String attributeName) {
    return new Attribute(attributeName);
  }

  /**
   * <p>Sample: <code>$("#mydiv").shouldHave(attribute("fileId", "12345"));</code></p>
   *
   * @param attributeName          name of attribute
   * @param expectedAttributeValue expected value of attribute
   */
  @CheckReturnValue
  @Nonnull
  public static Condition attribute(String attributeName, String expectedAttributeValue) {
    return new AttributeWithValue(attributeName, expectedAttributeValue);
  }

  /**
   * Assert that given element's attribute matches given regular expression
   *
   * <p>Sample: <code>$("h1").should(attributeMatching("fileId", ".*12345.*"))</code></p>
   *
   * @param attributeName  name of attribute
   * @param attributeRegex regex to match attribute value
   */
  @CheckReturnValue
  @Nonnull
  public static Condition attributeMatching(String attributeName, String attributeRegex) {
    return new MatchAttributeWithValue(attributeName, attributeRegex);
  }

  /**
   * <p>Sample: <code>$("#mydiv").shouldHave(href("/one/two/three.pdf"));</code></p>
   *
   * It looks similar to `$.shouldHave(attribute("href", href))`, but
   *   it overcomes the fact that Selenium returns full url (even if "href" attribute in html contains relative url).
   *
   * @param href expected value of "href" attribute
   */
  @CheckReturnValue
  @Nonnull
  public static Condition href(String href) {
    return new Href(href);
  }

  /**
   * Assert that element contains given "value" attribute as substring
   * NB! Ignores difference in non-visible characters like spaces, non-breakable spaces, tabs, newlines  etc.
   *
   * <p>Sample: <code>$("input").shouldHave(value("12345 666 77"));</code></p>
   *
   * @param expectedValue expected value of "value" attribute
   */
  @CheckReturnValue
  @Nonnull
  public static Condition value(String expectedValue) {
    return new Value(expectedValue);
  }

  /**
   * Check that element has given the property value of the pseudo-element
   * <p>Sample: <code>$("input").shouldHave(pseudo(":first-letter", "color", "#ff0000"));</code></p>
   *
   * @param pseudoElementName pseudo-element name of the element,
   *                          ":before", ":after", ":first-letter", ":first-line", ":selection"
   * @param propertyName property name of the pseudo-element
   * @param expectedValue expected value of the property
   */
  @CheckReturnValue
  @Nonnull
  public static Condition pseudo(String pseudoElementName, String propertyName, String expectedValue) {
    return new PseudoElementPropertyWithValue(pseudoElementName, propertyName, expectedValue);
  }

  /**
   * Check that element has given the "content" property of the pseudo-element
   * <p>Sample: <code>$("input").shouldHave(pseudo(":before", "Hello"));</code></p>
   *
   * @param pseudoElementName pseudo-element name of the element, ":before", ":after"
   * @param expectedValue expected content of the pseudo-element
   */
  @CheckReturnValue
  @Nonnull
  public static Condition pseudo(String pseudoElementName, String expectedValue) {
    return new PseudoElementPropertyWithValue(pseudoElementName, "content", expectedValue);
  }

  /**
   * <p>Sample: <code>$("#input").shouldHave(exactValue("John"));</code></p>
   *
   * @param value expected value of input field
   */
  @CheckReturnValue
  @Nonnull
  public static Condition exactValue(String value) {
    return attribute("value", value);
  }

  /**
   * Asserts the name attribute of the element to be exact string
   * <p>Sample: <code>$("#input").shouldHave(name("username"))</code></p>
   *
   * @param name expected name of input field
   */
  @CheckReturnValue
  @Nonnull
  public static Condition name(String name) {
    return attribute("name", name);
  }

  /**
   *  Asserts the type attribute of the element to be exact string
   * <p>Sample: <code>$("#input").shouldHave(type("checkbox"))</code></p>
   *
   * @param type expected type of input field
   */
  @CheckReturnValue
  @Nonnull
  public static Condition type(String type) {
    return attribute("type", type);
  }

  /**
   * <p>Sample: <code>$("#input").shouldHave(id("myForm"))</code></p>
   *
   * @param id expected id of input field
   */
  @CheckReturnValue
  @Nonnull
  public static Condition id(String id) {
    return attribute("id", id);
  }

  /**
   * 1) For input element, check that value is missing or empty
   * <p>Sample: <code>$("#input").shouldBe(empty)</code></p>
   * <p>
   * 2) For other elements, check that text is empty
   * <p>Sample: <code>$("h2").shouldBe(empty)</code></p>
   */
  public static final Condition empty = and("empty", exactValue(""), exactText(""));

  /**
   * The same as matchText()
   * <p>Sample: <code>$(".error_message").waitWhile(matchesText("Exception"), 12000)</code></p>
   *
   * @see #matchText(String)
   */
  @CheckReturnValue
  @Nonnull
  public static Condition matchesText(String text) {
    return matchText(text);
  }

  /**
   * Assert that given element's text matches given regular expression
   *
   * <p>Sample: <code>$("h1").should(matchText("Hello\s*John"))</code></p>
   *
   * @param regex e.g. Kicked.*Chuck Norris - in this case ".*" can contain any characters including spaces, tabs, CR etc.
   */
  @CheckReturnValue
  @Nonnull
  public static Condition matchText(String regex) {
    return new MatchText(regex);
  }

  /**
   * <p>
   * Assert that element contains given text as a substring.
   * </p>
   *
   * <p>Sample: <code>$("h1").shouldHave(text("Hello\s*John"))</code></p>
   *
   * <p>NB! Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element.
   *             NB! Empty string is not allowed (because any element does contain an empty text).
   *
   * @throws IllegalArgumentException if given text is null or empty
   */
  @CheckReturnValue
  @Nonnull
  public static Condition text(String text) {
    return new Text(text);
  }

  /**
   * Checks on a element that exactly given text is selected (=marked with mouse/keyboard)
   *
   * <p>Sample: {@code $("input").shouldHave(selectedText("Text"))}</p>
   *
   * <p>NB! Case sensitive</p>
   *
   * @param expectedText expected selected text of the element
   */
  @CheckReturnValue
  @Nonnull
  public static Condition selectedText(String expectedText) {
    return new SelectedText(expectedText);
  }

  /**
   * Assert that element contains given text as a case sensitive substring
   *
   * <p>Sample: <code>$("h1").shouldHave(textCaseSensitive("Hello\s*John"))</code></p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  @CheckReturnValue
  @Nonnull
  public static Condition textCaseSensitive(String text) {
    return new CaseSensitiveText(text);
  }

  /**
   * Assert that element has exactly (case insensitive) given text
   * <p>Sample: <code>$("h1").shouldHave(exactText("Hello"))</code></p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  @CheckReturnValue
  @Nonnull
  public static Condition exactText(String text) {
    return new ExactText(text);
  }

  /**
   * Assert that element contains given text (without checking child elements).
   * <p>Sample: <code>$("h1").shouldHave(ownText("Hello"))</code></p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element without its children
   */
  @CheckReturnValue
  @Nonnull
  public static Condition ownText(String text) {
    return new OwnText(text);
  }

  /**
   * Assert that element has given text (without checking child elements).
   * <p>Sample: <code>$("h1").shouldHave(ownText("Hello"))</code></p>
   *
   * <p>Case insensitive</p>
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element without its children
   */
  @CheckReturnValue
  @Nonnull
  public static Condition exactOwnText(String text) {
    return new ExactOwnText(text);
  }

  /**
   * Assert that element has exactly the given text
   * <p>Sample: <code>$("h1").shouldHave(exactTextCaseSensitive("Hello"))</code></p>
   *
   * <p>NB! Ignores multiple whitespaces between words</p>
   *
   * @param text expected text of HTML element
   */
  @CheckReturnValue
  @Nonnull
  public static Condition exactTextCaseSensitive(String text) {
    return new ExactTextCaseSensitive(text);
  }

  /**
   * Asserts that element has the given class. Element may other classes too.
   * <p>Sample: <code>$("input").shouldHave(cssClass("active"));</code></p>
   */
  @CheckReturnValue
  @Nonnull
  public static Condition cssClass(String cssClass) {
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
  public static Condition cssValue(String propertyName, @Nullable String expectedValue) {
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
  public static Condition match(String description, Predicate<WebElement> predicate) {
    return new CustomMatch(description, predicate);
  }

  /**
   * Check if image is loaded.
   */
  public static final Condition image = new IsImageLoaded();

  /**
   * Check if browser focus is currently in given element.
   */
  public static final Condition focused = new Focused();

  /**
   * Checks that element is not disabled
   *
   * @see WebElement#isEnabled()
   */
  public static final Condition enabled = new Enabled();

  /**
   * Checks that element is disabled
   *
   * @see WebElement#isEnabled()
   */
  public static final Condition disabled = new Disabled();

  /**
   * Checks that element is selected (inputs like drop-downs etc.)
   *
   * @see WebElement#isSelected()
   */
  public static final Condition selected = new Selected();

  /**
   * Checks that checkbox is checked
   *
   * @see WebElement#isSelected()
   */
  public static final Condition checked = new Checked();

  /**
   * Negate given condition.
   * <p>
   * Used for methods like $.shouldNot(exist), $.shouldNotBe(visible)
   * <p>
   * Typically you don't need to use it.
   */
  @CheckReturnValue
  @Nonnull
  public static Condition not(final Condition condition) {
    return condition.negate();
  }

  @Nonnull
  public Condition negate() {
    return new Not(this, absentElementMatchesCondition);
  }

  /**
   * Check if element matches ALL given conditions.
   *
   * @param name       Name of this condition, like "empty" (meaning e.g. empty text AND empty value).
   * @param conditions Conditions to match.
   * @return logical AND for given conditions.
   */
  @CheckReturnValue
  @Nonnull
  public static Condition and(String name, Condition... conditions) {
    return new And(name, asList(conditions));
  }

  /**
   * Check if element matches ANY of given conditions.
   *
   * @param name       Name of this condition, like "error" (meaning e.g. "error" OR "failed").
   * @param conditions Conditions to match.
   * @return logical OR for given conditions.
   */
  @CheckReturnValue
  @Nonnull
  public static Condition or(String name, Condition... conditions) {
    return new Or(name, asList(conditions));
  }

  /**
   * Used to form human-readable condition expression
   * Example element.should(be(visible),have(text("abc"))
   *
   * @param delegate next condition to wrap
   * @return Condition
   */
  @CheckReturnValue
  @Nonnull
  public static Condition be(Condition delegate) {
    return wrap("be", delegate);
  }

  /**
   * Used to form human-readable condition expression
   * Example element.should(be(visible),have(text("abc"))
   *
   * @param delegate next condition to wrap
   * @return Condition
   */
  @CheckReturnValue
  @Nonnull
  public static Condition have(Condition delegate) {
    return wrap("have", delegate);
  }

  private static Condition wrap(String prefix, Condition delegate) {
    return new NamedCondition(prefix, delegate);
  }

  private final String name;
  private final boolean absentElementMatchesCondition;

  public Condition(String name) {
    this(name, false);
  }

  public Condition(String name, boolean absentElementMatchesCondition) {
    this.name = name;
    this.absentElementMatchesCondition = absentElementMatchesCondition;
  }

  /**
   * Check if given element matches this condition.
   *
   * @param element given WebElement
   * @return true if element matches condition
   */
  public abstract boolean apply(Driver driver, WebElement element);

  public boolean applyNull() {
    return absentElementMatchesCondition;
  }

  /**
   * If element didn't match the condition, returns the actual value of element.
   * Used in error reporting.
   * Optional. Makes sense only if you need to add some additional important info to error message.
   *
   * @param driver given driver
   * @param element given WebElement
   * @return any string that needs to be appended to error message.
   */
  @Nullable
  public String actualValue(Driver driver, WebElement element) {
    return null;
  }

  /**
   * Should be used for explaining the reason of condition
   */
  @CheckReturnValue
  @Nonnull
  public Condition because(String message) {
    return new ExplainedCondition(this, message);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

  public boolean missingElementSatisfiesCondition() {
    return absentElementMatchesCondition;
  }
}
