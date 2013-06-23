package com.codeborne.selenide;

import org.openqa.selenium.By;

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
    assertDoesNotContainBothApostrophesAndQuotes(elementText);
    return elementText.contains("'") ?
        By.xpath(".//*/text()[contains(normalize-space(.), \"" + elementText + "\")]/parent::*") :
        By.xpath(".//*/text()[contains(normalize-space(.), '" + elementText + "')]/parent::*");
  }

  /**
   * Find element HAVING given text (exactly this text)
   *
   * NB! It seems that Selenium WebDriver does not support i18n characters in XPath :(
   *
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  public static By byText(String elementText) {
    assertDoesNotContainBothApostrophesAndQuotes(elementText);
    return elementText.contains("'") ?
        By.xpath(".//*/text()[normalize-space(.) = \"" + elementText + "\"]/parent::*") :
        By.xpath(".//*/text()[normalize-space(.) = '" + elementText + "']/parent::*");
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
    assertDoesNotContainBothApostrophesAndQuotes(attributeValue);
    return attributeValue.contains("'") ?
        By.xpath(".//*[@" + attributeName + " = \"" + attributeValue + "\"]") :
        By.xpath(".//*[@" + attributeName + " = '" + attributeValue + "']");
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

  private static void assertDoesNotContainBothApostrophesAndQuotes(String elementText) {
    if (elementText.contains("'") && elementText.contains("\"")) {
      throw new UnsupportedOperationException("Text with both apostrophes and quotes is not supported");
    }
  }

	public static By byValue(String value) {
	  return byAttribute("value", value);
  }
}