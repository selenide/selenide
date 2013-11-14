package com.codeborne.selenide;

import org.openqa.selenium.By;

import static com.codeborne.selenide.impl.Quotes.escape;

public class Selectors {
  /**
   * Find element CONTAINING given text (as a substring)
   *
   * NB! It seems that Selenium WebDriver does not support i18n characters in XPath :(
   *
   * @param elementText Text to search inside element
   * @return standard selenium By criteria
   */
  public static By withText(String elementText) {
    return By.xpath(".//*/text()[contains(normalize-space(.), " + escape.quotes(elementText) + ")]/parent::*");
  }

  /**
   * Find element that has EXACTLY this text
   *
   * NB! It seems that Selenium WebDriver does not support i18n characters in XPath :(
   *
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  public static By byText(String elementText) {
    return By.xpath(".//*/text()[normalize-space(.) = " + escape.quotes(elementText) + "]/parent::*");
  }

  /**
   * Find elements having attribute with given value.
   *
   * Seems to work incorrectly if attribute name contains dash, for example: <option data-mailServerId="123"></option>
   *
   * @param attributeName name of attribute, should not be empty or null
   * @param attributeValue value of attribute, should not contain both apostrophes and quotes
   * @return standard selenium By criteria
   */
  public static By byAttribute(String attributeName, String attributeValue) {
    return By.xpath(".//*[@" + attributeName + '=' + escape.quotes(attributeValue) + ']');
  }

  /**
   * Synonym for #byAttribute
   *
   * Seems to work incorrectly in HtmlUnit and PhantomJS if attribute name contains dash (e.g. "data-mailServerId")
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

	public static By byValue(String value) {
	  return byAttribute("value", value);
  }
}