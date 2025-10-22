package com.codeborne.selenide;

import com.codeborne.selenide.impl.SelenideProperties;
import com.codeborne.selenide.selector.ByAttribute;
import com.codeborne.selenide.selector.ByDeepShadowCss;
import com.codeborne.selenide.selector.ByLabel;
import com.codeborne.selenide.selector.ByShadowCss;
import com.codeborne.selenide.selector.ByTagAndText;
import com.codeborne.selenide.selector.ByText;
import com.codeborne.selenide.selector.SearchByAttribute;
import com.codeborne.selenide.selector.SearchByText;
import com.codeborne.selenide.selector.WithTagAndText;
import com.codeborne.selenide.selector.WithText;
import org.openqa.selenium.By;

import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;

public class Selectors {
  private static final SelenideProperties properties = new SelenideProperties();
  private static final String DEFAULT_TEST_ID = "data-test-id";

  /**
   * Find element CONTAINING given text (as a substring).
   * <p>
   * This method ignores difference between space, \n, \r, \t and &nbsp;
   * This method ignores multiple spaces.
   *
   * @param elementText Text to search inside element
   * @return standard selenium By criteria`
   */
  public static By withText(String elementText) {
    return new WithText(elementText);
  }

  /**
   * Find element CONTAINING given text (as a substring).
   * <p>
   * This method ignores difference between space, \n, \r, \t and &nbsp;
   * This method ignores multiple spaces.
   *
   * @param tag         Html tag name (e.g. "dev", "span", "li", "a", "td")
   * @param elementText Text to search inside element
   * @return standard selenium By criteria`
   */
  public static By withTagAndText(String tag, String elementText) {
    return new WithTagAndText(tag, elementText);
  }

  /**
   * Same as {@link #withText(String)}, but case-insensitive.
   * @since 5.22.0
   */
  public static By withTextCaseInsensitive(String elementText) {
    return new SearchByText(elementText, partialText().caseInsensitive());
  }

  /**
   * Find element that has given text (the whole text, not a substring).
   * <p>
   * This method ignores difference between space, \n, \r, \t and &nbsp;
   * This method ignores multiple spaces.
   * This method is case-sensitive.
   *
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  public static By byText(String elementText) {
    return new ByText(elementText);
  }

  /**
   * Find element that has given text (the whole text, not a substring).
   * <p>
   * This method ignores difference between space, \n, \r, \t and &nbsp;
   * This method ignores multiple spaces.
   * This method is case-sensitive.
   *
   * @param tag         Html tag name (e.g. "dev", "span", "li", "a", "td")
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  public static By byTagAndText(String tag, String elementText) {
    return new ByTagAndText(tag, elementText);
  }

  /**
   * Same as {@link #byText(String)}, but case-insensitive.
   * @since 5.22.0
   */
  public static By byTextCaseInsensitive(String elementText) {
    return new SearchByText(elementText, fullText().caseInsensitive());
  }

  public static By byLabel(String elementText) {
    return byLabel(elementText, fullText());
  }

  public static By byLabel(String elementText, TextMatchOptions options) {
    return new ByLabel(elementText, options);
  }

  /**
   * Find element by "placeholder" attribute
   * @param placeholderText the expected placeholder value to find by
   * @since 7.12.0
   */
  public static By byPlaceholder(String placeholderText) {
    return byPlaceholder(placeholderText, fullText());
  }

  /**
   * Find element by "placeholder" attribute
   * @param placeholderText the expected placeholder value to find by
   * @param options either case-sensitive or insensitive, either full text or substring etc.
   * @since 7.12.0
   */
  public static By byPlaceholder(String placeholderText, TextMatchOptions options) {
    return byAttribute("placeholder", placeholderText, options);
  }

  /**
   * Find elements having attribute with given value.
   * <p>
   * Examples:
   * {@code <div binding="fieldValue"></div>}
   * Find element with attribute 'binding' EXACTLY containing text 'fieldValue' , use:
   * byAttribute("binding", "fieldValue")
   * <p>
   * For finding difficult/generated data attribute which contains some value:
   * {@code <div binding="userName17fk5n6kc2Ds45F40d0fieldValue_promoLanding word"></div>}
   * <p>
   * Find element with attribute 'binding' CONTAINING text 'fieldValue', use symbol '*' with attribute name:
   * byAttribute("binding*", "fieldValue") it same as By.cssSelector("[binding*='fieldValue']")
   * <p>
   * Find element whose attribute 'binding' BEGINS with 'userName', use symbol '^' with attribute name:
   * byAttribute("binding^", "fieldValue")
   * <p>
   * Find element whose attribute 'binding' ENDS with 'promoLanding', use symbol '$' with attribute name:
   * byAttribute("binding$", "promoLanding")
   * <p>
   * Find element whose attribute 'binding' CONTAINING WORD 'word':
   * byAttribute("binding~", "word")
   * <p>
   * Seems to work incorrectly if attribute name contains dash, for example: {@code <option data-mailServerId="123"></option>}
   *
   * @param attributeName  name of attribute, should not be empty or null
   * @param attributeValue value of attribute, should not contain both apostrophes and quotes
   * @return standard selenium By cssSelector criteria
   */
  public static By byAttribute(String attributeName, String attributeValue) {
    return new ByAttribute(attributeName, attributeValue);
  }

  /**
   * @since 7.12.0
   */
  public static By byAttribute(String attributeName, String attributeValue, TextMatchOptions options) {
    return new SearchByAttribute(attributeName, attributeValue, options);
  }

  /**
   * @see ByShadowCss#cssSelector(java.lang.String, java.lang.String...)
   * @since 5.10
   */
  public static By shadowCss(String target, String... shadowHostsChain) {
    return ByShadowCss.cssSelector(target, shadowHostsChain);
  }

  /**
   * @see ByDeepShadowCss#ByDeepShadowCss(java.lang.String)
   * @since v6.8.0
   */
  public static By shadowDeepCss(String target) {
    return new ByDeepShadowCss(target);
  }

  /**
   * Synonym for #byAttribute
   */
  public static By by(String attributeName, String attributeValue) {
    return byAttribute(attributeName, attributeValue);
  }

  /**
   * Find element with given title ("title" attribute)
   */
  public static By byTitle(String title) {
    return byAttribute("title", title);
  }

  /**
   * Find element with given title ("title" attribute)
   * @since 7.12.0
   */
  public static By byTitle(String title, TextMatchOptions options) {
    return byAttribute("title", title, options);
  }

  /**
   * Find element with given "alt" attribute
   *
   * @since 7.12.0
   */
  public static By byAltText(String title) {
    return byAttribute("alt", title);
  }

  /**
   * Find element with given "alt" attribute
   *
   * @since 7.12.0
   */
  public static By byAltText(String title, TextMatchOptions options) {
    return byAttribute("alt", title, options);
  }

  /**
   * Find element with given "data-test-id" attribute
   *
   * Name of "data-test-id" attribute can be customized:
   * <ul>
   *   <li>via system property: {@code -Dselenide.test-id.attribute=data-testid}, or</li>
   *   <li>in file "selenide.properties": {@code selenide.test-id.attribute=data-testid}</li>
   * </ul>
   * @since 7.12.0
   */
  public static By byTestId(String testId) {
    return byAttribute(properties.getProperty("selenide.test-id.attribute", DEFAULT_TEST_ID), testId);
  }

  /**
   * Find input element with given value ("value" attribute)
   */
  public static By byValue(String value) {
    return byAttribute("value", value);
  }

  /**
   * @see By#name(java.lang.String)
   */
  public static By byName(String name) {
    return By.name(name);
  }

  /**
   * @see By#xpath(java.lang.String)
   */
  public static By byXpath(String xpath) {
    return By.xpath(xpath);
  }

  /**
   * @see By#linkText(java.lang.String)
   */
  public static By byLinkText(String linkText) {
    return By.linkText(linkText);
  }

  /**
   * @see By#partialLinkText(java.lang.String)
   */
  public static By byPartialLinkText(String partialLinkText) {
    return By.partialLinkText(partialLinkText);
  }

  /**
   * @see By#id(java.lang.String)
   */
  public static By byId(String id) {
    return By.id(id);
  }

  /**
   * @see By#cssSelector(java.lang.String)
   */
  public static By byCssSelector(String css) {
    return By.cssSelector(css);
  }

  /**
   * @see By#className(java.lang.String)
   */
  public static By byClassName(String className) {
    return By.className(className);
  }

  /**
   * @see By#tagName(java.lang.String)
   */
  public static By byTagName(String tagName) {
    return By.tagName(tagName);
  }
}
