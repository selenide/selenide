package com.codeborne.selenide;

import com.codeborne.selenide.selector.ByAttribute;
import com.codeborne.selenide.selector.ByDeepShadow;
import com.codeborne.selenide.selector.ByShadow;
import com.codeborne.selenide.selector.ByTagAndText;
import com.codeborne.selenide.selector.ByText;
import com.codeborne.selenide.selector.ByTextCaseInsensitive;
import com.codeborne.selenide.selector.WithTagAndText;
import com.codeborne.selenide.selector.WithText;
import com.codeborne.selenide.selector.WithTextCaseInsensitive;
import org.openqa.selenium.By;

public class Selectors {
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
   * @param tag Html tag name (e.g. "dev", "span", "li", "a", "td")
   * @param elementText Text to search inside element
   * @return standard selenium By criteria`
   */
  public static By withTagAndText(String tag, String elementText) {
    return new WithTagAndText(tag, elementText);
  }

  /**
   * Same as {@link #withText(String)}, but case-insensitive.
   */
  public static By withTextCaseInsensitive(String elementText) {
    return new WithTextCaseInsensitive(elementText);
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
   * @param tag Html tag name (e.g. "dev", "span", "li", "a", "td")
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  public static By byTagAndText(String tag, String elementText) {
    return new ByTagAndText(tag, elementText);
  }

  /**
   * Same as {@link #byText(String)}, but case-insensitive.
   */
  public static By byTextCaseInsensitive(String elementText) {
    return new ByTextCaseInsensitive(elementText);
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
   * @see ByShadow#cssSelector(java.lang.String, java.lang.String, java.lang.String...)
   */
  public static By shadowCss(String target, String shadowHost, String... innerShadowHosts) {
    return ByShadow.cssSelector(target, shadowHost, innerShadowHosts);
  }

  /**
   * @see ByDeepShadow#cssSelector(java.lang.String)
   */
  public static By shadowDeepCss(String target) {
    return ByDeepShadow.cssSelector(target);
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
