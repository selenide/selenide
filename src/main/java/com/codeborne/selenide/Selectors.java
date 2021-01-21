package com.codeborne.selenide;

import com.codeborne.selenide.selector.ByAttribute;
import com.codeborne.selenide.selector.ByShadow;
import com.codeborne.selenide.selector.ByText;
import com.codeborne.selenide.selector.ByTextCaseInsensitive;
import com.codeborne.selenide.selector.WithText;
import com.codeborne.selenide.selector.WithTextCaseInsensitive;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
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
  @CheckReturnValue
  @Nonnull
  public static By withText(String elementText) {
    return new WithText(elementText);
  }

  @CheckReturnValue
  @Nonnull
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
  @CheckReturnValue
  @Nonnull
  public static By byText(String elementText) {
    return new ByText(elementText);
  }

  /**
   * Same as {@link #byText(String)}, but case-insensitive.
   */
  @CheckReturnValue
  @Nonnull
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
  @CheckReturnValue
  @Nonnull
  public static By byAttribute(String attributeName, String attributeValue) {
    return new ByAttribute(attributeName, attributeValue);
  }

  /**
   * @see ByShadow#cssSelector(java.lang.String, java.lang.String, java.lang.String...)
   * @since 5.10
   */
  @CheckReturnValue
  @Nonnull
  public static By shadowCss(String target, String shadowHost, String... innerShadowHosts) {
    return ByShadow.cssSelector(target, shadowHost, innerShadowHosts);
  }

  /**
   * Synonym for #byAttribute
   */
  @CheckReturnValue
  @Nonnull
  public static By by(String attributeName, String attributeValue) {
    return byAttribute(attributeName, attributeValue);
  }

  /**
   * Find element with given title ("title" attribute)
   */
  @CheckReturnValue
  @Nonnull
  public static By byTitle(String title) {
    return byAttribute("title", title);
  }

  /**
   * Find input element with given value ("value" attribute)
   */
  @CheckReturnValue
  @Nonnull
  public static By byValue(String value) {
    return byAttribute("value", value);
  }

  /**
   * @see By#name(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  @Nonnull
  public static By byName(String name) {
    return By.name(name);
  }

  /**
   * @see By#xpath(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  @Nonnull
  public static By byXpath(String xpath) {
    return By.xpath(xpath);
  }

  /**
   * @see By#linkText(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  @Nonnull
  public static By byLinkText(String linkText) {
    return By.linkText(linkText);
  }

  /**
   * @see By#partialLinkText(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  @Nonnull
  public static By byPartialLinkText(String partialLinkText) {
    return By.partialLinkText(partialLinkText);
  }

  /**
   * @see By#id(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  @Nonnull
  public static By byId(String id) {
    return By.id(id);
  }

  /**
   * @see By#cssSelector(java.lang.String)
   * @since 3.8
   */
  @CheckReturnValue
  @Nonnull
  public static By byCssSelector(String css) {
    return By.cssSelector(css);
  }

  /**
   * @see By#className(java.lang.String)
   * @since 3.8
   */
  @CheckReturnValue
  @Nonnull
  public static By byClassName(String className) {
    return By.className(className);
  }

  /**
   * @see By#tagName(java.lang.String)
   * @since 5.11
   */
  @CheckReturnValue
  @Nonnull
  public static By byTagName(String tagName) {
    return By.tagName(tagName);
  }
}
