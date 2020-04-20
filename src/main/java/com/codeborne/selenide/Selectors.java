package com.codeborne.selenide;

import com.codeborne.selenide.selector.ByShadow;
import com.google.errorprone.annotations.CheckReturnValue;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;

public class Selectors {
  private static final String NORMALIZE_SPACE_XPATH = "normalize-space(translate(string(.), '\t\n\r\u00a0', '    '))";

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
  public static By withText(String elementText) {
    return new WithText(elementText);
  }

  /**
   * Find element that has given text (the whole text, not a substring).
   * <p>
   * This method ignores difference between space, \n, \r, \t and &nbsp;
   * This method ignores multiple spaces.
   *
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  @CheckReturnValue
  public static By byText(String elementText) {
    return new ByText(elementText);
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
  public static By byAttribute(String attributeName, String attributeValue) {
    String escapedAttributeValue = attributeValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    return By.cssSelector(String.format("[%s=\"%s\"]", attributeName, escapedAttributeValue));
  }

  /**
   * @see ByShadow#cssSelector(java.lang.String, java.lang.String, java.lang.String...)
   * @since 5.10
   */
  @CheckReturnValue
  public static By shadowCss(String target, String shadowHost, String... innerShadowHosts) {
    return ByShadow.cssSelector(target, shadowHost, innerShadowHosts);
  }

  /**
   * Synonym for #byAttribute
   */
  @CheckReturnValue
  public static By by(String attributeName, String attributeValue) {
    return byAttribute(attributeName, attributeValue);
  }

  /**
   * Find element with given title ("title" attribute)
   */
  @CheckReturnValue
  public static By byTitle(String title) {
    return byAttribute("title", title);
  }

  /**
   * Find input element with given value ("value" attribute)
   */
  @CheckReturnValue
  public static By byValue(String value) {
    return byAttribute("value", value);
  }

  public static class ByText extends By.ByXPath {
    protected final String elementText;

    public ByText(String elementText) {
      super(".//*/text()[" + NORMALIZE_SPACE_XPATH + " = " + Quotes.escape(elementText) + "]/parent::*");
      this.elementText = elementText;
    }

    @Override
    public String toString() {
      return "by text: " + elementText;
    }

    String getXPath() {
      return super.toString().replace("By.xpath: ", "");
    }
  }

  public static class WithText extends By.ByXPath {
    protected final String elementText;

    public WithText(String elementText) {
      super(".//*/text()[contains(" + NORMALIZE_SPACE_XPATH + ", " + Quotes.escape(elementText) + ")]/parent::*");
      this.elementText = elementText;
    }

    @Override
    public String toString() {
      return "with text: " + elementText;
    }

    String getXPath() {
      return super.toString().replace("By.xpath: ", "");
    }
  }

  /**
   * @see By#name(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  public static By byName(String name) {
    return By.name(name);
  }

  /**
   * @see By#xpath(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  public static By byXpath(String xpath) {
    return By.xpath(xpath);
  }

  /**
   * @see By#linkText(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  public static By byLinkText(String linkText) {
    return By.linkText(linkText);
  }

  /**
   * @see By#partialLinkText(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  public static By byPartialLinkText(String partialLinkText) {
    return By.partialLinkText(partialLinkText);
  }

  /**
   * @see By#id(java.lang.String)
   * @since 3.1
   */
  @CheckReturnValue
  public static By byId(String id) {
    return By.id(id);
  }

  /**
   * @see By#cssSelector(java.lang.String)
   * @since 3.8
   */
  @CheckReturnValue
  public static By byCssSelector(String css) {
    return By.cssSelector(css);
  }

  /**
   * @see By#className(java.lang.String)
   * @since 3.8
   */
  @CheckReturnValue
  public static By byClassName(String className) {
    return By.className(className);
  }

  /**
   * @see By#tagName(java.lang.String)
   * @since 5.11
   */
  @CheckReturnValue
  public static By byTagName(String tagName) {
    return By.tagName(tagName);
  }
}
